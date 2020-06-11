import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import FlowerInOrder from './flower-in-order';
import FlowerInOrderDetail from './flower-in-order-detail';
import FlowerInOrderUpdate from './flower-in-order-update';
import FlowerInOrderDeleteDialog from './flower-in-order-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={FlowerInOrderDeleteDialog} />
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={FlowerInOrderUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={FlowerInOrderUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={FlowerInOrderDetail} />
      <ErrorBoundaryRoute path={match.url} component={FlowerInOrder} />
    </Switch>
  </>
);

export default Routes;
