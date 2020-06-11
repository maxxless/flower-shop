import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import CollectionInOrder from './collection-in-order';
import CollectionInOrderDetail from './collection-in-order-detail';
import CollectionInOrderUpdate from './collection-in-order-update';
import CollectionInOrderDeleteDialog from './collection-in-order-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={CollectionInOrderDeleteDialog} />
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={CollectionInOrderUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={CollectionInOrderUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={CollectionInOrderDetail} />
      <ErrorBoundaryRoute path={match.url} component={CollectionInOrder} />
    </Switch>
  </>
);

export default Routes;
