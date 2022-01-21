module.exports = {
  mode: 'jit',
  purge: ['./pages/**/*.{js,ts,jsx,tsx}', './components/**/*.{js,ts,jsx,tsx}'],
  darkMode: false, // or 'media' or 'class'
  theme: {
    minWidth: {
      '0': '0',
      '1/12': '8%',
      '1/6': '17%',
      '1/4': '25%',
      'full': '100%',
     }
  },
  variants: {
    extend: {},
  },
  plugins: [],
}
