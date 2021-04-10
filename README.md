# scramble-web

Flexiana's [coding test](https://docs.google.com/document/d/1q78CyntLJt4qniVvuY4c8nYtR09B2RLT_nDNBsI-Of4/edit).

## Overview

It's split into a frontend and backend accordingly in the codebase.

It's made using reagent and reitit, and the code is built using Figwheel.

Test requirements:

1. The basic logic is in `src/backend/scramble.clj`

2. The backend is in `src/backend/server.clj`. Once running (see below), you can try it out at http://0.0.0.0:3000/api-docs/index.html

3. The frontend is in `src/frontend/core.cljs`. Once running, you can try it out at http://0.0.0.0:3000/index.html

## Development

You can run all tests and launch the server by issuing

    lein check-run

Ignore the test page that pops out, and open http://0.0.0.0:3000/index.html

To merely run the backend

    lein run

You can launch a cljs REPL provided that the application is running as above by
issuing

    lein fig:build

## Future improvements

Get the tests to run headless.

## License

Copyright © 2021 Gabriel Peredo

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
