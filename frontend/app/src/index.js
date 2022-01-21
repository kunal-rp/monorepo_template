import React from 'react';
import { render } from 'react-dom';
import { BrowserRouter } from "react-router-dom";

import HelloWorld from './HelloWorld';
import Protected from './components/Protected';

render(
	<BrowserRouter>
		<Protected>
			<HelloWorld />
		</Protected>
	</BrowserRouter>, document.getElementById('root'));