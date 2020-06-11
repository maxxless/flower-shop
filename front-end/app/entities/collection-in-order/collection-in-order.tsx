import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, InputGroup, Col, Row, Table } from 'reactstrap';
import { AvForm, AvGroup, AvInput } from 'availity-reactstrap-validation';
import { ICrudSearchAction, ICrudGetAllAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getSearchEntities, getEntities } from './collection-in-order.reducer';
import { ICollectionInOrder } from 'app/shared/model/collection-in-order.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface ICollectionInOrderProps extends StateProps, DispatchProps, RouteComponentProps<{ url: string }> {}

export const CollectionInOrder = (props: ICollectionInOrderProps) => {
  const [search, setSearch] = useState('');

  useEffect(() => {
    props.getEntities();
  }, []);

  const startSearching = () => {
    if (search) {
      props.getSearchEntities(search);
    }
  };

  const clear = () => {
    setSearch('');
    props.getEntities();
  };

  const handleSearch = event => setSearch(event.target.value);

  const { collectionInOrderList, match, loading } = props;
  return (
    <div>
      <h2 id="collection-in-order-heading">
        Collection In Orders
        <Link to={`${match.url}/new`} className="btn btn-primary float-right jh-create-entity" id="jh-create-entity">
          <FontAwesomeIcon icon="plus" />
          &nbsp; Create new Collection In Order
        </Link>
      </h2>
      <Row>
        <Col sm="12">
          <AvForm onSubmit={startSearching}>
            <AvGroup>
              <InputGroup>
                <AvInput type="text" name="search" value={search} onChange={handleSearch} placeholder="Search" />
                <Button className="input-group-addon">
                  <FontAwesomeIcon icon="search" />
                </Button>
                <Button type="reset" className="input-group-addon" onClick={clear}>
                  <FontAwesomeIcon icon="trash" />
                </Button>
              </InputGroup>
            </AvGroup>
          </AvForm>
        </Col>
      </Row>
      <div className="table-responsive">
        {collectionInOrderList && collectionInOrderList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>ID</th>
                <th>Amount</th>
                <th>Collection</th>
                <th>Packing</th>
                <th>Order</th>
                <th />
              </tr>
            </thead>
            <tbody>
              {collectionInOrderList.map((collectionInOrder, i) => (
                <tr key={`entity-${i}`}>
                  <td>
                    <Button tag={Link} to={`${match.url}/${collectionInOrder.id}`} color="link" size="sm">
                      {collectionInOrder.id}
                    </Button>
                  </td>
                  <td>{collectionInOrder.amount}</td>
                  <td>
                    {collectionInOrder.collection ? (
                      <Link to={`collection/${collectionInOrder.collection.id}`}>{collectionInOrder.collection.id}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td>
                    {collectionInOrder.packing ? (
                      <Link to={`packing/${collectionInOrder.packing.id}`}>{collectionInOrder.packing.id}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td>
                    {collectionInOrder.order ? <Link to={`order/${collectionInOrder.order.id}`}>{collectionInOrder.order.id}</Link> : ''}
                  </td>
                  <td className="text-right">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`${match.url}/${collectionInOrder.id}`} color="info" size="sm">
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">View</span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${collectionInOrder.id}/edit`} color="primary" size="sm">
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${collectionInOrder.id}/delete`} color="danger" size="sm">
                        <FontAwesomeIcon icon="trash" /> <span className="d-none d-md-inline">Delete</span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && <div className="alert alert-warning">No Collection In Orders found</div>
        )}
      </div>
    </div>
  );
};

const mapStateToProps = ({ collectionInOrder }: IRootState) => ({
  collectionInOrderList: collectionInOrder.entities,
  loading: collectionInOrder.loading
});

const mapDispatchToProps = {
  getSearchEntities,
  getEntities
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(CollectionInOrder);
