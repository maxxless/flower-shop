import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Packing from './packing';
import PackingDetail from './packing-detail';
import PackingUpdate from './packing-update';
import PackingDeleteDialog from './packing-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={PackingDeleteDialog} />
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={PackingUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={PackingUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={PackingDetail} />
      <ErrorBoundaryRoute path={match.url} component={Packing} />
    </Switch>
  </>
);

export default Routes;
