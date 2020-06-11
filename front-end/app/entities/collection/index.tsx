import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Collection from './collection';
import CollectionDetail from './collection-detail';
import CollectionUpdate from './collection-update';
import CollectionDeleteDialog from './collection-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={CollectionDeleteDialog} />
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={CollectionUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={CollectionUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={CollectionDetail} />
      <ErrorBoundaryRoute path={match.url} component={Collection} />
    </Switch>
  </>
);

export default Routes;
