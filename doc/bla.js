const ReactDOM = require('react-dom/client');
const React = require('react')

function App() {
    return (
        <div>Hello World!</div>
    );
  }

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
    <App />
);
