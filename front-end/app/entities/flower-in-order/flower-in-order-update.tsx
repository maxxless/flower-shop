import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { IColour } from 'app/shared/model/colour.model';
import { getEntities as getColours } from 'app/entities/colour/colour.reducer';
import { IFlower } from 'app/shared/model/flower.model';
import { getEntities as getFlowers } from 'app/entities/flower/flower.reducer';
import { IOrder } from 'app/shared/model/order.model';
import { getEntities as getOrders } from 'app/entities/order/order.reducer';
import { getEntity, updateEntity, createEntity, reset } from './flower-in-order.reducer';
import { IFlowerInOrder } from 'app/shared/model/flower-in-order.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IFlowerInOrderUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const FlowerInOrderUpdate = (props: IFlowerInOrderUpdateProps) => {
  const [colourId, setColourId] = useState('0');
  const [flowerId, setFlowerId] = useState('0');
  const [orderId, setOrderId] = useState('0');
  const [isNew, setIsNew] = useState(!props.match.params || !props.match.params.id);

  const { flowerInOrderEntity, colours, flowers, orders, loading, updating } = props;

  const handleClose = () => {
    props.history.push('/flower-in-order');
  };

  useEffect(() => {
    if (isNew) {
      props.reset();
    } else {
      props.getEntity(props.match.params.id);
    }

    props.getColours();
    props.getFlowers();
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
        ...flowerInOrderEntity,
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
          <h2 id="flowershopApp.flowerInOrder.home.createOrEditLabel">Create or edit a FlowerInOrder</h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : flowerInOrderEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="flower-in-order-id">ID</Label>
                  <AvInput id="flower-in-order-id" type="text" className="form-control" name="id" required readOnly />
                </AvGroup>
              ) : null}
              <AvGroup>
                <Label id="amountLabel" for="flower-in-order-amount">
                  Amount
                </Label>
                <AvField id="flower-in-order-amount" type="string" className="form-control" name="amount" />
              </AvGroup>
              <AvGroup>
                <Label for="flower-in-order-colour">Colour</Label>
                <AvInput id="flower-in-order-colour" type="select" className="form-control" name="colour.id">
                  <option value="" key="0" />
                  {colours
                    ? colours.map(otherEntity => (
                        <option value={otherEntity.id} key={otherEntity.id}>
                          {otherEntity.id}
                        </option>
                      ))
                    : null}
                </AvInput>
              </AvGroup>
              <AvGroup>
                <Label for="flower-in-order-flower">Flower</Label>
                <AvInput id="flower-in-order-flower" type="select" className="form-control" name="flower.id">
                  <option value="" key="0" />
                  {flowers
                    ? flowers.map(otherEntity => (
                        <option value={otherEntity.id} key={otherEntity.id}>
                          {otherEntity.id}
                        </option>
                      ))
                    : null}
                </AvInput>
              </AvGroup>
              <AvGroup>
                <Label for="flower-in-order-order">Order</Label>
                <AvInput id="flower-in-order-order" type="select" className="form-control" name="order.id">
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
              <Button tag={Link} id="cancel-save" to="/flower-in-order" replace color="info">
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
  colours: storeState.colour.entities,
  flowers: storeState.flower.entities,
  orders: storeState.order.entities,
  flowerInOrderEntity: storeState.flowerInOrder.entity,
  loading: storeState.flowerInOrder.loading,
  updating: storeState.flowerInOrder.updating,
  updateSuccess: storeState.flowerInOrder.updateSuccess
});

const mapDispatchToProps = {
  getColours,
  getFlowers,
  getOrders,
  getEntity,
  updateEntity,
  createEntity,
  reset
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(FlowerInOrderUpdate);
