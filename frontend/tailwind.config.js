module.exports = {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        brand: {
          light: '#fef6d5',
          night: '#043566',
          lightCardColor: '#fee1bf',
          nightCardColor: '#03478c',
          sandDark: 'rgba(158, 141, 96, 0.2)',
          sandLight: 'rgba(244, 226, 198, 0.4)',
          actionButtonLight: 'rgb(250, 242, 159)',
          actionButtonDark: 'rgb(239, 207, 119)',
          lightBackGround: 'rgba(1, 163, 173, 0.6)'
        },
        slate: {
            950: '#0f172a'
        }
      },
      fontFamily: {
          kameron: ['Kameron', 'serif'],
          aladin: ['Aladin', 'serif']
      }
    },
  },
  plugins: [],
}