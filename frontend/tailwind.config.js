/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./app/**/*.{ts,tsx}", "./components/**/*.{ts,tsx}", "./features/**/*.{ts,tsx}"],
  theme: {
    extend: {
      colors: {
        ink: "#0c0e12",
        panel: "#141820",
        line: "#232a36",
        paper: "#f4efe6",
        emerald: "#3d9a78",
        indigo: "#6b7cff",
      },
      fontFamily: {
        sans: ["IBM Plex Sans", "ui-sans-serif", "system-ui"],
        serif: ["Iowan Old Style", "Palatino", "Georgia", "serif"],
      },
    },
  },
  plugins: [],
};
