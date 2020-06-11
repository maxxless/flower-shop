import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import CollectionInCart from './collection-in-cart';
import CollectionInCartDetail from './collection-in-cart-detail';
import CollectionInCartUpdate from './collection-in-cart-update';
import CollectionInCartDeleteDialog from './collection-in-cart-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={CollectionInCartDeleteDialog} />
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={CollectionInCartUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={CollectionInCartUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={CollectionInCartDetail} />
      <ErrorBoundaryRoute path={match.url} component={CollectionInCart} />
    </Switch>
  </>
);

export default Routes;
