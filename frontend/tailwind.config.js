
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        brand: {
          primary: '#ebab5e', //main gold color
          secondary: '#d49e54', // The darker shade
          light: '#fef3c7', // A light accent
          dark: '#a57c44', // text or dark elements
        },
        slate: {
            950: '#0f172a' // Example of adding a custom shade
        }
      },
      fontFamily: {
          kameron: ['Kameron', 'serif'],
      }
    },
  },
  plugins: [],
}