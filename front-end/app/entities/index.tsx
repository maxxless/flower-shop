import React from 'react';
import { Switch } from 'react-router-dom';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Collection from './collection';
import Category from './category';
import Cart from './cart';
import Colour from './colour';
import Flower from './flower';
import Order from './order';
import Delivery from './delivery';
import ClientCard from './client-card';
import Packing from './packing';
import FlowerInOrder from './flower-in-order';
import CollectionInOrder from './collection-in-order';
import FlowerInCart from './flower-in-cart';
import CollectionInCart from './collection-in-cart';
/* jhipster-needle-add-route-import - JHipster will add routes here */

const Routes = ({ match }) => (
  <div>
    <Switch>
      {/* prettier-ignore */}
      <ErrorBoundaryRoute path={`${match.url}collection`} component={Collection} />
      <ErrorBoundaryRoute path={`${match.url}category`} component={Category} />
      <ErrorBoundaryRoute path={`${match.url}cart`} component={Cart} />
      <ErrorBoundaryRoute path={`${match.url}colour`} component={Colour} />
      <ErrorBoundaryRoute path={`${match.url}flower`} component={Flower} />
      <ErrorBoundaryRoute path={`${match.url}order`} component={Order} />
      <ErrorBoundaryRoute path={`${match.url}delivery`} component={Delivery} />
      <ErrorBoundaryRoute path={`${match.url}client-card`} component={ClientCard} />
      <ErrorBoundaryRoute path={`${match.url}packing`} component={Packing} />
      <ErrorBoundaryRoute path={`${match.url}flower-in-order`} component={FlowerInOrder} />
      <ErrorBoundaryRoute path={`${match.url}collection-in-order`} component={CollectionInOrder} />
      <ErrorBoundaryRoute path={`${match.url}flower-in-cart`} component={FlowerInCart} />
      <ErrorBoundaryRoute path={`${match.url}collection-in-cart`} component={CollectionInCart} />
      {/* jhipster-needle-add-route-path - JHipster will add routes here */}
    </Switch>
  </div>
);

export default Routes;
