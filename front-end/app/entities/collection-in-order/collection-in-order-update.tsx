import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { ICollection } from 'app/shared/model/collection.model';
import { getEntities as getCollections } from 'app/entities/collection/collection.reducer';
import { IPacking } from 'app/shared/model/packing.model';
import { getEntities as getPackings } from 'app/entities/packing/packing.reducer';
import { IOrder } from 'app/shared/model/order.model';
import { getEntities as getOrders } from 'app/entities/order/order.reducer';
import { getEntity, updateEntity, createEntity, reset } from './collection-in-order.reducer';
import { ICollectionInOrder } from 'app/shared/model/collection-in-order.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface ICollectionInOrderUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const CollectionInOrderUpdate = (props: ICollectionInOrderUpdateProps) => {
  const [collectionId, setCollectionId] = useState('0');
  const [packingId, setPackingId] = useState('0');
  const [orderId, setOrderId] = useState('0');
  const [isNew, setIsNew] = useState(!props.match.params || !props.match.params.id);

  const { collectionInOrderEntity, collections, packings, orders, loading, updating } = props;

  const handleClose = () => {
    props.history.push('/collection-in-order');
  };

  useEffect(() => {
    if (isNew) {
      props.reset();
    } else {
      props.getEntity(props.match.params.id);
    }

    props.getCollections();
    props.getPackings();
    props.getOrders();
  }, []);

  useEffect(() => {
    if (props.updateSuccess) {
      handleClose();
    }
  }, [props.updateSuccess]);

  const saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const entity = {
        ...collectionInOrderEntity,
        ...values
      };

      if (isNew) {
        props.createEntity(entity);
      } else {
        props.updateEntity(entity);
      }
    }
  };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="flowershopApp.collectionInOrder.home.createOrEditLabel">Create or edit a CollectionInOrder</h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : collectionInOrderEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="collection-in-order-id">ID</Label>
                  <AvInput id="collection-in-order-id" type="text" className="form-control" name="id" required readOnly />
                </AvGroup>
              ) : null}
              <AvGroup>
                <Label id="amountLabel" for="collection-in-order-amount">
                  Amount
                </Label>
                <AvField id="collection-in-order-amount" type="string" className="form-control" name="amount" />
              </AvGroup>
              <AvGroup>
                <Label for="collection-in-order-collection">Collection</Label>
                <AvInput id="collection-in-order-collection" type="select" className="form-control" name="collection.id">
                  <option value="" key="0" />
                  {collections
                    ? collections.map(otherEntity => (
                        <option value={otherEntity.id} key={otherEntity.id}>
                          {otherEntity.id}
                        </option>
                      ))
                    : null}
                </AvInput>
              </AvGroup>
              <AvGroup>
                <Label for="collection-in-order-packing">Packing</Label>
                <AvInput id="collection-in-order-packing" type="select" className="form-control" name="packing.id">
                  <option value="" key="0" />
                  {packings
                    ? packings.map(otherEntity => (
                        <option value={otherEntity.id} key={otherEntity.id}>
                          {otherEntity.id}
                        </option>
                      ))
                    : null}
                </AvInput>
              </AvGroup>
              <AvGroup>
                <Label for="collection-in-order-order">Order</Label>
                <AvInput id="collection-in-order-order" type="select" className="form-control" name="order.id">
                  <option value="" key="0" />
                  {orders
                    ? orders.map(otherEntity => (
                        <option value={otherEntity.id} key={otherEntity.id}>
                          {otherEntity.id}
                        </option>
                      ))
                    : null}
                </AvInput>
              </AvGroup>
              <Button tag={Link} id="cancel-save" to="/collection-in-order" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">Back</span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp; Save
              </Button>
            </AvForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

const mapStateToProps = (storeState: IRootState) => ({
  collections: storeState.collection.entities,
  packings: storeState.packing.entities,
  orders: storeState.order.entities,
  collectionInOrderEntity: storeState.collectionInOrder.entity,
  loading: storeState.collectionInOrder.loading,
  updating: storeState.collectionInOrder.updating,
  updateSuccess: storeState.collectionInOrder.updateSuccess
});

const mapDispatchToProps = {
  getCollections,
  getPackings,
  getOrders,
  getEntity,
  updateEntity,
  createEntity,
  reset
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(CollectionInOrderUpdate);
