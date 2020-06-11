import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import ClientCard from './client-card';
import ClientCardDetail from './client-card-detail';
import ClientCardUpdate from './client-card-update';
import ClientCardDeleteDialog from './client-card-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={ClientCardDeleteDialog} />
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={ClientCardUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={ClientCardUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={ClientCardDetail} />
      <ErrorBoundaryRoute path={match.url} component={ClientCard} />
    </Switch>
  </>
);

export default Routes;
