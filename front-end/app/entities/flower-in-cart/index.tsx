import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import FlowerInCart from './flower-in-cart';
import FlowerInCartDetail from './flower-in-cart-detail';
import FlowerInCartUpdate from './flower-in-cart-update';
import FlowerInCartDeleteDialog from './flower-in-cart-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={FlowerInCartDeleteDialog} />
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={FlowerInCartUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={FlowerInCartUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={FlowerInCartDetail} />
      <ErrorBoundaryRoute path={match.url} component={FlowerInCart} />
    </Switch>
  </>
);

export default Routes;
