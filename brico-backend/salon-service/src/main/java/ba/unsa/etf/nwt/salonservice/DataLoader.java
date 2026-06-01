package ba.unsa.etf.nwt.salonservice;

import ba.unsa.etf.nwt.salonservice.model.*;
import ba.unsa.etf.nwt.salonservice.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;

// ═══════════════════════════════════════════════════════════════════════
//  HAIRDRESSER ID raspored (po redoslijedu ubacivanja):
//  Originalni (IDs 1–10, 2 po salonu):
//   1 → Lejla Mehić      (Elite Cut,         userId=7)
//   2 → Tarik Bašić      (Elite Cut,         userId=8)
//   3 → Edin Kovač       (Urban Barber,      userId=9)
//   4 → Amina Zukić      (Urban Barber,      userId=10)
//   5 → Nina Softić      (Glam Studio,       userId=11)
//   6 → Sara Begić       (Glam Studio,       userId=12)
//   7 → Haris Muratović  (Barber Kingdom,    userId=13)
//   8 → Denis Avdić      (Barber Kingdom,    userId=14)
//   9 → Jovana Nikolić   (Style Lab,         userId=15)
//  10 → Aleksandar Perić (Style Lab,         userId=16)
//  3. frizeri za postojeće salone (IDs 11–15):
//  11 → Naida Halilović  (Elite Cut,         userId=22)
//  12 → Muhamed Husić    (Urban Barber,      userId=23)
//  13 → Lejla Čaušević   (Glam Studio,       userId=24)
//  14 → Nermin Salihović (Barber Kingdom,    userId=25)
//  15 → Tijana Marković  (Style Lab,         userId=26)
//  Novi saloni (IDs 16–24):
//  16 → Viktor Blažević  (The Gentlemen's Club, userId=27)
//  17 → Dario Šimić      (The Gentlemen's Club, userId=28)
//  18 → Bojan Krsmanović (The Gentlemen's Club, userId=29)
//  19 → Belma Selimović  (Luxe Beauty Lounge,   userId=30)
//  20 → Dina Terzić      (Luxe Beauty Lounge,   userId=31)
//  21 → Amra Husejnović  (Luxe Beauty Lounge,   userId=32)
//  22 → Stefan Đorđević  (Brico Studio,      userId=33)
//  23 → Lana Begović     (Brico Studio,      userId=34)
//  24 → Niko Perišić     (Brico Studio,      userId=35)
//
//  SERVICE ID raspored (po redoslijedu ubacivanja):
//  Elite Cut originalne (1–5):
//   1=Šišanje(15)  2=Pranje+Fen(10)  3=Balayage(80)  4=Keratin tretman(120)  5=Brijanje britvom(18)
//  Urban Barber originalne (6–9):
//   6=Muško šišanje(12)  7=Brijanje britvom(15)  8=Fade šišanje(18)  9=Dječije šišanje(10)
//  Glam Studio originalne (10–13):
//  10=Bojenje(50)  11=Pramenovi(70)  12=Šišanje+fen(20)  13=Brazilski keratin(100)
//  Barber Kingdom originalne (14–17):
//  14=Klasično šišanje(14)  15=Moderna frizura(16)  16=Brijanje+maska(22)  17=Komplet usluga(35)
//  Style Lab originalne (18–21):
//  18=Kreativno bojenje(60)  19=Šišanje(15)  20=Kompleksna boja(90)  21=Fen styling(25)
//  Elite Cut extra (22–26):
//  22=Bojenje kose(55)  23=Pramenovi folijom(65)  24=Feniranje s rolnama(22)
//  25=Dječije šišanje EC(12)  26=Tretman kose maskom(30)
//  Urban Barber extra (27–32):
//  27=Skin fade(20)  28=Oblikovanje brade(12)  29=Hot towel brijanje(22)
//  30=Šamponiranje(8)  31=Muški wax tretman(15)  32=Duga muška frizura(25)
//  Glam Studio extra (33–38):
//  33=Rekonstrukcija kose(60)  34=Keratin tretman GS(90)  35=Ombre bojenje(80)
//  36=Tretman duboke njege(35)  37=Svečana frizura GS(55)  38=Nadogradnja punđe(45)
//  Barber Kingdom extra (39–44):
//  39=Oblikovanje brade britvom(15)  40=Fade+brada(28)  41=Dječije šišanje BK(10)
//  42=Balm tretman brade(18)  43=Royal shave BK(35)  44=Muška njega lica BK(25)
//  Style Lab extra (45–50):
//  45=Pastel bojenje(75)  46=Undercut šišanje(25)  47=Muška boja(40)
//  48=Perm tretman(80)  49=Šišanje pixie(20)  50=Balayage SL(70)
//  The Gentlemen's Club (51–60):
//  51=Klasično muško šišanje(20)  52=Royal shave TGC(45)  53=Skin fade+konture(25)
//  54=Oblikovanje brade TGC(20)  55=Hot towel brijanje TGC(30)  56=VIP komplet paket(80)
//  57=Muška njega lica TGC(35)  58=Šišanje s maskom TGC(35)  59=Bojenje brade(25)  60=Dječije TGC(15)
//  Luxe Beauty Lounge (61–70):
//  61=Žensko šišanje LBL(35)  62=Luksuzno feniranje(30)  63=Balayage premium(120)
//  64=Keratin luxury(130)  65=Farbanje solo bojom(65)  66=Pramenovi folijom LBL(90)
//  67=Svečana frizura LBL(60)  68=Brazilski keratin luxury(150)  69=Tretman Olaplex(70)  70=Duboka hidratacija(50)
//  Brico Studio (71–80):
//  71=Unisex šišanje(18)  72=Bojenje kose BS(55)  73=Feniranje BS(18)
//  74=Muško šišanje+brada(25)  75=Ombre efekt(75)  76=Keratin tretman BS(100)
//  77=Brijanje BS(15)  78=Balayage BS(80)  79=Dječije BS(12)  80=Svečana frizura BS(50)
// ═══════════════════════════════════════════════════════════════════════

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final SalonRepository           salonRepository;
    private final HairdresserRepository     hairdresserRepository;
    private final SalonServiceRepository    serviceRepository;
    private final WorkingHoursRepository    workingHoursRepository;
    private final SalonPhotoRepository      photoRepository;

    @Override
    public void run(String... args) {
        if (salonRepository.count() > 0) return;
        log.info("=== Seed: salon-service ===");

        // ── 1. Elite Cut ─────────────────────────────────────────────────────
        Salon eliteCut = createSalon("Elite Cut", "elite-cut",
                "Moderan frizerski salon u centru Sarajeva — specijalnost balayage, keratin tretmani i profesionalno bojenje",
                "Sarajevo", "Titova 1", "+38761100011", 2L, true, "4200111000001",
                4.9, 7);

        addHairdresser(eliteCut, 7L, "Lejla Mehić",
                "Specijalnost: balayage, keratin i žensko šišanje",
                "Balayage, Keratin, Žensko šišanje", 5.0, 4);
        addHairdresser(eliteCut, 8L, "Tarik Bašić",
                "Muško šišanje, brijanje i precizno oblikovanje brade",
                "Muško šišanje, Brijanje, Oblikovanje brade", 4.5, 2);

        // Originalne usluge: IDs 1–5
        addService(eliteCut, "Šišanje",           15,  30);
        addService(eliteCut, "Pranje + Fen",       10,  20);
        addService(eliteCut, "Balayage",           80, 120);
        addService(eliteCut, "Keratin tretman",   120, 150);
        addService(eliteCut, "Brijanje britvom",   18,  30);

        addWorkingHours(eliteCut);
        addPhotos(eliteCut,
                "https://images.unsplash.com/photo-1605497787289-3e1e9e8c5e87?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1522337360788-8b13dee7a37e?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1600948836101-f9ffda59d250?auto=format&fit=crop&w=800&q=80");

        // ── 2. Urban Barber ──────────────────────────────────────────────────
        Salon urbanBarber = createSalon("Urban Barber", "urban-barber",
                "Premium barbershop iskustvo u Mostaru — fade, skin fade, klasično brijanje i muška njega kose",
                "Mostar", "Bulevar 12", "+38762200012", 3L, true, "4200222000002",
                4.3, 6);

        addHairdresser(urbanBarber, 9L, "Edin Kovač",
                "Specijalnost: fade, skin fade i muška njega kose",
                "Fade, Skin fade, Muška njega", 4.3, 3);
        addHairdresser(urbanBarber, 10L, "Amina Zukić",
                "Klasično brijanje, hot-towel tretmani i precizno muško šišanje",
                "Klasično brijanje, Hot-towel, Muško šišanje", 4.5, 2);

        // Originalne usluge: IDs 6–9
        addService(urbanBarber, "Muško šišanje",    12, 30);
        addService(urbanBarber, "Brijanje britvom", 15, 30);
        addService(urbanBarber, "Fade šišanje",     18, 45);
        addService(urbanBarber, "Dječije šišanje",  10, 20);

        addWorkingHours(urbanBarber);
        addPhotos(urbanBarber,
                "https://images.unsplash.com/photo-1503951914875-452162b0f3f1?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1621605815971-fbc98d665033?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1599351431202-1e0f0137899a?auto=format&fit=crop&w=800&q=80");

        // ── 3. Glam Studio ───────────────────────────────────────────────────
        Salon glamStudio = createSalon("Glam Studio", "glam-studio",
                "Ženski frizerski salon specijalizovan za boju, pramenove, ombre i profesionalnu stilizaciju kose",
                "Sarajevo", "Ferhadija 22", "+38761300013", 4L, true, "4200333000003",
                4.4, 5);

        addHairdresser(glamStudio, 11L, "Nina Softić",
                "Specijalnost: bojenje, pramenovi, balayage i ombre tehnika",
                "Bojenje, Pramenovi, Balayage, Ombre", 4.0, 3);
        addHairdresser(glamStudio, 12L, "Sara Begić",
                "Fen styling, svečane frizure i nadogradnja punđe",
                "Fen styling, Svečane frizure, Punđa", 5.0, 1);

        // Originalne usluge: IDs 10–13
        addService(glamStudio, "Bojenje",           50,  90);
        addService(glamStudio, "Pramenovi",         70, 120);
        addService(glamStudio, "Šišanje + fen",     20,  45);
        addService(glamStudio, "Brazilski keratin", 100, 180);

        addWorkingHours(glamStudio);
        addPhotos(glamStudio,
                "https://images.unsplash.com/photo-1562322140-8baeecca8a0d?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1487412947147-5cebf100ffc2?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1534297635766-a262cdcb8ee4?auto=format&fit=crop&w=800&q=80");

        // ── 4. Barber Kingdom ────────────────────────────────────────────────
        Salon barberKingdom = createSalon("Barber Kingdom", "barber-kingdom",
                "Tradicionalni barbershop s modernim pristupom — brijanje, fade, oblikovanje brade i muška njega lica",
                "Tuzla", "Armijska bb", "+38765400014", 5L, true, "4200444000004",
                4.1, 7);

        addHairdresser(barberKingdom, 13L, "Haris Muratović",
                "Klasično i moderno šišanje, komplet usluge i oblikovanje brade",
                "Klasično šišanje, Moderno šišanje, Komplet usluge", 4.25, 4);
        addHairdresser(barberKingdom, 14L, "Denis Avdić",
                "Brijanje britvom, hot-towel tretman i muška njega lica",
                "Brijanje britvom, Hot-towel, Muška njega lica", 4.0, 2);

        // Originalne usluge: IDs 14–17
        addService(barberKingdom, "Klasično šišanje",   14, 30);
        addService(barberKingdom, "Moderna frizura",    16, 35);
        addService(barberKingdom, "Brijanje + maska",   22, 45);
        addService(barberKingdom, "Komplet usluga",     35, 75);

        addWorkingHours(barberKingdom);
        addPhotos(barberKingdom,
                "https://images.unsplash.com/photo-1556742049-0cfed4f6a45d?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1635273051831-22c94e9b2e25?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1584813470613-5b1c1cad3d69?auto=format&fit=crop&w=800&q=80");

        // ── 5. Style Lab ─────────────────────────────────────────────────────
        Salon styleLab = createSalon("Style Lab", "style-lab",
                "Kreativni salon za izražavanje individualnosti — nestandardne boje, avangardni stilovi i pastel tehnika",
                "Banja Luka", "Krajiška 5", "+38765500015", 6L, true, "4200555000005",
                4.0, 4);

        addHairdresser(styleLab, 15L, "Jovana Nikolić",
                "Kreativne boje, fantasy boje i pastel tehnika",
                "Kreativne boje, Fantasy, Pastel", 4.0, 1);
        addHairdresser(styleLab, 16L, "Aleksandar Perić",
                "Muške frizure, undercut i kreativni muški stilovi",
                "Undercut, Muške frizure, Kreativni stilovi", 3.5, 2);

        // Originalne usluge: IDs 18–21
        addService(styleLab, "Kreativno bojenje", 60, 120);
        addService(styleLab, "Šišanje",           15,  30);
        addService(styleLab, "Kompleksna boja",   90, 150);
        addService(styleLab, "Fen styling",       25,  30);

        addWorkingHours(styleLab);
        addPhotos(styleLab,
                "https://images.unsplash.com/photo-1633681926022-84c23e8cb2d6?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1604654894610-df63bc536371?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1593702288056-f5dc72c59f96?auto=format&fit=crop&w=800&q=80");

        // ════════════════════════════════════════════════════════════════════
        //  EXTRA USLUGE za postojeće salone (IDs 22–50)
        // ════════════════════════════════════════════════════════════════════

        // Elite Cut extra: IDs 22–26
        addService(eliteCut, "Bojenje kose",         55,  90);
        addService(eliteCut, "Pramenovi folijom",    65, 120);
        addService(eliteCut, "Feniranje s rolnama",  22,  30);
        addService(eliteCut, "Dječije šišanje",      12,  20);
        addService(eliteCut, "Tretman kose maskom",  30,  45);

        // Urban Barber extra: IDs 27–32
        addService(urbanBarber, "Skin fade",           20, 45);
        addService(urbanBarber, "Oblikovanje brade",   12, 20);
        addService(urbanBarber, "Hot towel brijanje",  22, 40);
        addService(urbanBarber, "Šamponiranje",         8, 15);
        addService(urbanBarber, "Muški wax tretman",   15, 20);
        addService(urbanBarber, "Duga muška frizura",  25, 60);

        // Glam Studio extra: IDs 33–38
        addService(glamStudio, "Rekonstrukcija kose",    60,  90);
        addService(glamStudio, "Keratin tretman",        90, 150);
        addService(glamStudio, "Ombre bojenje",          80, 120);
        addService(glamStudio, "Tretman duboke njege",   35,  45);
        addService(glamStudio, "Svečana frizura",        55,  90);
        addService(glamStudio, "Nadogradnja punđe",      45,  60);

        // Barber Kingdom extra: IDs 39–44
        addService(barberKingdom, "Oblikovanje brade britvom", 15, 25);
        addService(barberKingdom, "Fade + brada",              28, 60);
        addService(barberKingdom, "Dječije šišanje",           10, 20);
        addService(barberKingdom, "Balm tretman brade",        18, 30);
        addService(barberKingdom, "Royal shave",               35, 60);
        addService(barberKingdom, "Muška njega lica",          25, 35);

        // Style Lab extra: IDs 45–50
        addService(styleLab, "Pastel bojenje",    75, 120);
        addService(styleLab, "Undercut šišanje",  25,  35);
        addService(styleLab, "Muška boja",        40,  60);
        addService(styleLab, "Perm tretman",      80, 120);
        addService(styleLab, "Šišanje pixie",     20,  30);
        addService(styleLab, "Balayage",          70, 120);

        // ════════════════════════════════════════════════════════════════════
        //  3. FRIZERI za postojeće salone (IDs 11–15)
        // ════════════════════════════════════════════════════════════════════

        addHairdresser(eliteCut, 22L, "Naida Halilović",
                "Bojenje kose jednom bojom, balayage i fen styling",
                "Bojenje, Balayage, Fen styling", 5.0, 1);

        addHairdresser(urbanBarber, 23L, "Muhamed Husić",
                "Skin fade, oblikovanje brade i muška njega kose",
                "Skin fade, Oblikovanje brade, Muška njega", 4.0, 1);

        addHairdresser(glamStudio, 24L, "Lejla Čaušević",
                "Ombre tehnika, rekonstrukcija kose i svečane frizure",
                "Ombre, Rekonstrukcija kose, Svečane frizure", 5.0, 1);

        addHairdresser(barberKingdom, 25L, "Nermin Salihović",
                "Klasično šišanje, oblikovanje brade i muška njega lica",
                "Klasično šišanje, Oblikovanje brade, Njega lica", 4.0, 1);

        addHairdresser(styleLab, 26L, "Tijana Marković",
                "Kreativne boje, pastel tehnika i balayage",
                "Kreativne boje, Pastel, Balayage", 5.0, 1);

        // ── 6. The Gentlemen's Club ───────────────────────────────────────────
        Salon tgc = createSalon("The Gentlemen's Club", "gentlemens-club",
                "Luksuzni muški barbershop u srcu Sarajeva — premium brijanje, royal shave i ekskluzivna muška njega",
                "Sarajevo", "Ilićeva 8", "+38761600016", 19L, true, "4200666000006",
                4.8, 4);

        addHairdresser(tgc, 27L, "Viktor Blažević",
                "Senior barber — specijalnost royal shave i skin fade",
                "Royal shave, Skin fade, VIP usluge", 5.0, 1);
        addHairdresser(tgc, 28L, "Dario Šimić",
                "Klasično brijanje, hot-towel tretman i oblikovanje brade",
                "Klasično brijanje, Hot-towel, Oblikovanje brade", 5.0, 1);
        addHairdresser(tgc, 29L, "Bojan Krsmanović",
                "Muško šišanje, skin fade i bojenje brade",
                "Muško šišanje, Skin fade, Bojenje brade", 4.5, 2);

        // Usluge The Gentlemen's Club: IDs 51–60
        addService(tgc, "Klasično muško šišanje",  20,  35);
        addService(tgc, "Royal shave",             45,  60);
        addService(tgc, "Skin fade + konture",     25,  45);
        addService(tgc, "Oblikovanje brade",       20,  30);
        addService(tgc, "Hot towel brijanje",      30,  45);
        addService(tgc, "VIP komplet paket",       80, 120);
        addService(tgc, "Muška njega lica",        35,  45);
        addService(tgc, "Šišanje s maskom",        35,  60);
        addService(tgc, "Bojenje brade",           25,  30);
        addService(tgc, "Dječije šišanje",         15,  20);

        addWorkingHours(tgc);
        addPhotos(tgc,
                "https://images.unsplash.com/photo-1585747860715-2ba37e788b70?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1557264337-e8a93017fe92?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1596728325488-58c87691e9af?auto=format&fit=crop&w=800&q=80");

        // ── 7. Luxe Beauty Lounge ─────────────────────────────────────────────
        Salon luxe = createSalon("Luxe Beauty Lounge", "luxe-beauty-lounge",
                "Luksuzni ženski salon u Mostaru — premium balayage, keratin tretmani i ekskluzivna njega kose",
                "Mostar", "Kralja Tomislava 3", "+38762700017", 20L, true, "4200777000007",
                4.8, 4);

        addHairdresser(luxe, 30L, "Belma Selimović",
                "Premium balayage, svečane frizure i balayage",
                "Balayage, Svečane frizure, Premium usluge", 5.0, 2);
        addHairdresser(luxe, 31L, "Dina Terzić",
                "Keratin tretmani, pramenovi folijom i duboka njega kose",
                "Keratin tretmani, Pramenovi, Duboka njega", 5.0, 1);
        addHairdresser(luxe, 32L, "Amra Husejnović",
                "Olaplex tretmani, žensko šišanje i luksuzno feniranje",
                "Olaplex, Žensko šišanje, Luksuzno feniranje", 4.0, 1);

        // Usluge Luxe Beauty Lounge: IDs 61–70
        addService(luxe, "Žensko šišanje",            35,  60);
        addService(luxe, "Luksuzno feniranje",         30,  45);
        addService(luxe, "Balayage premium",          120, 180);
        addService(luxe, "Keratin tretman luxury",    130, 180);
        addService(luxe, "Farbanje solo bojom",        65,  90);
        addService(luxe, "Pramenovi folijom",          90, 150);
        addService(luxe, "Svečana frizura",            60,  90);
        addService(luxe, "Brazilski keratin luxury",  150, 210);
        addService(luxe, "Tretman Olaplex",            70,  90);
        addService(luxe, "Duboka hidratacija kose",    50,  60);

        addWorkingHours(luxe);
        addPhotos(luxe,
                "https://images.unsplash.com/photo-1522337660859-02fbefca4702?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1560066984-138dadb4c035?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1519699047748-de8e457a634e?auto=format&fit=crop&w=800&q=80");

        // ── 8. Brico Studio ───────────────────────────────────────────────────
        Salon brico = createSalon("Brico Studio", "brico-studio",
                "Moderan unisex salon u Banja Luci — šišanje, bojenje i njega kose za sve stilove i uzraste",
                "Banja Luka", "Jovana Dučića 15", "+38765800018", 21L, true, "4200888000008",
                4.0, 3);

        addHairdresser(brico, 33L, "Stefan Đorđević",
                "Muško šišanje, brada i unisex usluge",
                "Muško šišanje, Brada, Unisex", 4.0, 1);
        addHairdresser(brico, 34L, "Lana Begović",
                "Bojenje kose, balayage i ženski stilovi",
                "Bojenje kose, Balayage, Ženski stilovi", 4.0, 1);
        addHairdresser(brico, 35L, "Niko Perišić",
                "Ombre tehnika, feniranje i svečane frizure",
                "Ombre, Feniranje, Svečane frizure", 4.0, 1);

        // Usluge Brico Studio: IDs 71–80
        addService(brico, "Unisex šišanje",        18,  35);
        addService(brico, "Bojenje kose",          55,  90);
        addService(brico, "Feniranje",             18,  30);
        addService(brico, "Muško šišanje + brada", 25,  45);
        addService(brico, "Ombre efekt",           75, 120);
        addService(brico, "Keratin tretman",      100, 150);
        addService(brico, "Brijanje",              15,  25);
        addService(brico, "Balayage",              80, 120);
        addService(brico, "Dječije šišanje",       12,  20);
        addService(brico, "Svečana frizura",       50,  75);

        addWorkingHours(brico);
        addPhotos(brico,
                "https://images.unsplash.com/photo-1582095133179-bfd08e2fb6b8?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1559599101-f09722fb4948?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1478146059778-26028b07395a?auto=format&fit=crop&w=800&q=80");

        log.info("=== Seed završen: {} salona, {} frizera, {} usluga, {} fotografija ===",
                salonRepository.count(), hairdresserRepository.count(),
                serviceRepository.count(), photoRepository.count());
    }

    // ── Pomoćne metode ───────────────────────────────────────────────────────

    private Salon createSalon(String name, String slug, String desc,
                               String city, String address, String phone,
                               Long ownerId, boolean verified, String idBroj,
                               double avgRating, int reviewCount) {
        return salonRepository.save(Salon.builder()
                .name(name).slug(slug).description(desc)
                .city(city).address(address).phone(phone)
                .ownerId(ownerId).verified(verified).isActive(true)
                .idBroj(idBroj).avgRating(avgRating).reviewCount(reviewCount)
                .build());
    }

    private void addHairdresser(Salon salon, Long userId, String name, String bio,
                                 String specialties, double avgRating, int totalAppointments) {
        hairdresserRepository.save(Hairdresser.builder()
                .salon(salon).userId(userId).fullName(name).bio(bio)
                .specialties(specialties).isActive(true)
                .avgRating(avgRating).totalAppointments(totalAppointments)
                .build());
    }

    private void addService(Salon salon, String name, double price, int durationMin) {
        serviceRepository.save(SalonService.builder()
                .salon(salon).name(name)
                .price(BigDecimal.valueOf(price))
                .durationMinutes(durationMin)
                .isActive(true)
                .build());
    }

    private void addPhotos(Salon salon, String url1, String url2, String url3) {
        photoRepository.save(SalonPhoto.builder()
                .salon(salon).url(url1).isPrimary(true).displayOrder(1).build());
        photoRepository.save(SalonPhoto.builder()
                .salon(salon).url(url2).isPrimary(false).displayOrder(2).build());
        photoRepository.save(SalonPhoto.builder()
                .salon(salon).url(url3).isPrimary(false).displayOrder(3).build());
    }

    private void addWorkingHours(Salon salon) {
        for (DayOfWeek day : DayOfWeek.values()) {
            boolean isDayOff = (day == DayOfWeek.SUNDAY);
            int dayIndex = day.getValue() % 7;
            workingHoursRepository.save(WorkingHours.builder()
                    .salon(salon).dayOfWeek(dayIndex)
                    .startTime(isDayOff ? null : LocalTime.of(8, 0))
                    .endTime(isDayOff ? null : LocalTime.of(20, 0))
                    .isDayOff(isDayOff).build());
        }
    }
}
