import type { Config } from "tailwindcss";

export default {
    content: ["./index.html", "./src/**/*.{ts,tsx}"],
    theme: {
        extend: {
            fontFamily: {
                display: ["Outfit", "sans-serif"],
                body: ["Space Grotesk", "sans-serif"],
            },
            colors: {
                brand: {
                    50: "#fff0f3",
                    100: "#ffd6de",
                    500: "#e94560",
                    600: "#d63651",
                    900: "#1a0a0d",
                },
            },
            borderRadius: {
                card: "1rem",
                xl: "0.75rem",
                "2xl": "1rem",
                "3xl": "1.5rem",
            },
            keyframes: {
                "fade-up": {
                    "0%": { opacity: "0", transform: "translateY(16px)" },
                    "100%": { opacity: "1", transform: "translateY(0)" },
                },
                "scale-in": {
                    "0%": { opacity: "0", transform: "scale(0.95)" },
                    "100%": { opacity: "1", transform: "scale(1)" },
                },
            },
            animation: {
                "fade-up": "fade-up 0.4s ease-out",
                "scale-in": "scale-in 0.3s ease-out",
            },
        },
    },
    plugins: [require("tailwindcss-animate")],
} satisfies Config;