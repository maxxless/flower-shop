import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Colour from './colour';
import ColourDetail from './colour-detail';
import ColourUpdate from './colour-update';
import ColourDeleteDialog from './colour-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={ColourDeleteDialog} />
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={ColourUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={ColourUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={ColourDetail} />
      <ErrorBoundaryRoute path={match.url} component={Colour} />
    </Switch>
  </>
);

export default Routes;
