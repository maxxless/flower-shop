import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Flower from './flower';
import FlowerDetail from './flower-detail';
import FlowerUpdate from './flower-update';
import FlowerDeleteDialog from './flower-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={FlowerDeleteDialog} />
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={FlowerUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={FlowerUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={FlowerDetail} />
      <ErrorBoundaryRoute path={match.url} component={Flower} />
    </Switch>
  </>
);

export default Routes;
