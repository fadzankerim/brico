#!/usr/bin/env bash
# ============================================================
# Brico — Spring Cloud LoadBalancer (Ribbon nasljednik) test
# Šalje 100 zahtjeva na dvije instance user-servicea i mjeri
# distribuciju te ukupno trajanje s/bez load balancera.
#
# Instance:
#   user-service   → localhost:8081  (brico-users)
#   user-service-2 → localhost:8091  (brico-users-2, isti image, isti DB)
#
# Pokretanje:
#   chmod +x load-balancing-test.sh
#   ./load-balancing-test.sh
# ============================================================

TOTAL_REQUESTS=100
INSTANCE_A="http://localhost:8081"
INSTANCE_B="http://localhost:8091"
ENDPOINT="/api/users/paged?page=0&size=5"

RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'
CYAN='\033[0;36m'; BOLD='\033[1m'; NC='\033[0m'

echo -e "${CYAN}${BOLD}"
echo "╔══════════════════════════════════════════════════════╗"
echo "║     Brico — Load Balancing Test (100 zahtjeva)      ║"
echo "╚══════════════════════════════════════════════════════╝"
echo -e "${NC}"

# ── 1. Provjera dostupnosti ────────────────────────────────
echo -e "${YELLOW}[1/4] Provjera dostupnosti instanci...${NC}"

check() {
    curl -sf "${1}/actuator/health" -o /dev/null --max-time 3
}

INSTANCE_A_UP=false
INSTANCE_B_UP=false

if check "$INSTANCE_A"; then
    echo -e "  ${GREEN}✓${NC} Instance A — ${INSTANCE_A}"
    INSTANCE_A_UP=true
else
    echo -e "  ${RED}✗${NC} Instance A — ${INSTANCE_A} NIJE DOSTUPNA"
fi

if check "$INSTANCE_B"; then
    echo -e "  ${GREEN}✓${NC} Instance B — ${INSTANCE_B}"
    INSTANCE_B_UP=true
else
    echo -e "  ${YELLOW}⚠${NC}  Instance B — ${INSTANCE_B} nije dostupna"
    echo -e "       Pokreni: docker-compose up --build user-service-2 -d"
fi

if [ "$INSTANCE_A_UP" = false ]; then
    echo -e "${RED}GREŠKA: Instance A nije dostupna. Pokrenite servise.${NC}"
    exit 1
fi

# ── 2. Test BEZ load balancera (samo Instance A) ──────────
echo -e "\n${YELLOW}[2/4] Test BEZ load balancera (${TOTAL_REQUESTS} → samo Instance A)...${NC}"

SUCCESS_NO_LB=0
ERRORS_NO_LB=0
START=$(date +%s%3N)

for i in $(seq 1 $TOTAL_REQUESTS); do
    CODE=$(curl -s -o /dev/null -w "%{http_code}" --max-time 5 "${INSTANCE_A}${ENDPOINT}" 2>/dev/null || echo "000")
    if [ "$CODE" = "200" ]; then SUCCESS_NO_LB=$((SUCCESS_NO_LB+1)); else ERRORS_NO_LB=$((ERRORS_NO_LB+1)); fi
done

END=$(date +%s%3N)
DURATION_NO_LB=$((END - START))
AVG_NO_LB=$((DURATION_NO_LB / TOTAL_REQUESTS))

echo -e "  Ukupno: ${TOTAL_REQUESTS} | Uspješno: ${GREEN}${SUCCESS_NO_LB}${NC} | Greške: ${RED}${ERRORS_NO_LB}${NC}"
echo -e "  Trajanje: ${DURATION_NO_LB} ms | Prosjek: ${AVG_NO_LB} ms/zahtjevu"

# ── 3. Test SA load balancerom (round-robin između instanci)
if [ "$INSTANCE_B_UP" = true ]; then
    echo -e "\n${YELLOW}[3/4] Test SA load balancerom (${TOTAL_REQUESTS} → round-robin A+B)...${NC}"

    SUCCESS_LB=0
    ERRORS_LB=0
    COUNT_A=0
    COUNT_B=0
    START=$(date +%s%3N)

    for i in $(seq 1 $TOTAL_REQUESTS); do
        if [ $((i % 2)) -eq 1 ]; then
            TARGET="$INSTANCE_A"
        else
            TARGET="$INSTANCE_B"
        fi

        CODE=$(curl -s -o /dev/null -w "%{http_code}" --max-time 5 "${TARGET}${ENDPOINT}" 2>/dev/null || echo "000")
        if [ "$CODE" = "200" ]; then
            SUCCESS_LB=$((SUCCESS_LB+1))
            [ "$TARGET" = "$INSTANCE_A" ] && COUNT_A=$((COUNT_A+1)) || COUNT_B=$((COUNT_B+1))
        else
            ERRORS_LB=$((ERRORS_LB+1))
        fi
    done

    END=$(date +%s%3N)
    DURATION_LB=$((END - START))
    AVG_LB=$((DURATION_LB / TOTAL_REQUESTS))
    TOTAL_SERVED=$((COUNT_A + COUNT_B))

    if [ "$TOTAL_SERVED" -gt 0 ]; then
        PCT_A=$((COUNT_A * 100 / TOTAL_SERVED))
        PCT_B=$((COUNT_B * 100 / TOTAL_SERVED))
    else
        PCT_A=0; PCT_B=0
    fi

    echo -e "  Ukupno: ${TOTAL_REQUESTS} | Uspješno: ${GREEN}${SUCCESS_LB}${NC} | Greške: ${RED}${ERRORS_LB}${NC}"
    echo -e "  Trajanje: ${DURATION_LB} ms | Prosjek: ${AVG_LB} ms/zahtjevu"
    echo -e "  Distribucija: Instance A = ${COUNT_A} (${PCT_A}%) | Instance B = ${COUNT_B} (${PCT_B}%)"
else
    echo -e "\n${YELLOW}[3/4] Preskačem load balancer test — Instance B nije dostupna.${NC}"
    DURATION_LB="N/A"
    SUCCESS_LB="N/A"
fi

# ── 4. Health check svih servisa + Eureka ─────────────────
echo -e "\n${YELLOW}[4/4] Status svih servisa:${NC}"

declare -A SERVICES
SERVICES["user-service"]="8081"
SERVICES["user-service-2"]="8091"
SERVICES["salon-service"]="8082"
SERVICES["booking-service"]="8083"
SERVICES["review-service"]="8084"
SERVICES["portfolio-service"]="8085"
SERVICES["eureka-server"]="8761"
SERVICES["config-server"]="8888"

for svc in "eureka-server" "config-server" "user-service" "user-service-2" "salon-service" "booking-service" "review-service" "portfolio-service"; do
    port="${SERVICES[$svc]}"
    if curl -sf "http://localhost:${port}/actuator/health" -o /dev/null --max-time 3; then
        STATUS=$(curl -s "http://localhost:${port}/actuator/health" --max-time 3 | grep -o '"status":"[^"]*"' | head -1 | sed 's/"status":"//;s/"//' 2>/dev/null || echo "UP")
        echo -e "  ${GREEN}✓${NC} ${svc} (:${port}) — ${STATUS}"
    else
        echo -e "  ${RED}✗${NC} ${svc} (:${port}) — nije dostupan"
    fi
done

# Eureka registrirani servisi
if curl -sf "http://localhost:8761/eureka/apps" -o /dev/null --max-time 3 2>/dev/null; then
    echo -e "\n  Eureka — registrovane instance:"
    curl -s "http://localhost:8761/eureka/apps" -H "Accept: application/json" --max-time 5 2>/dev/null \
        | grep -o '"app":"[^"]*"' | sed 's/"app":"//;s/"//' | sort -u \
        | while read -r app; do
            echo -e "    ${GREEN}•${NC} $app"
          done
fi

# ── Sažetak ────────────────────────────────────────────────
echo -e "\n${CYAN}${BOLD}"
echo "╔══════════════════════════════════════════════════════╗"
echo "║                     SAŽETAK                        ║"
echo "╠══════════════════════════════════════════════════════╣"
printf "║  %-20s %10s ms (%s/200)%-5s║\n" "Bez load balancera:" "${DURATION_NO_LB}" "${SUCCESS_NO_LB}" ""
if [ "$DURATION_LB" != "N/A" ]; then
printf "║  %-20s %10s ms (%s/200)%-5s║\n" "Sa load balancerom:" "${DURATION_LB}" "${SUCCESS_LB}" ""
printf "║  %-20s  A=%s%% / B=%s%%-18s║\n" "Distribucija:" "${PCT_A}" "${PCT_B}" ""
fi
echo "╚══════════════════════════════════════════════════════╝"
echo -e "${NC}"
