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
import { ICart } from 'app/shared/model/cart.model';
import { getEntities as getCarts } from 'app/entities/cart/cart.reducer';
import { getEntity, updateEntity, createEntity, reset } from './flower-in-cart.reducer';
import { IFlowerInCart } from 'app/shared/model/flower-in-cart.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IFlowerInCartUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const FlowerInCartUpdate = (props: IFlowerInCartUpdateProps) => {
  const [colourId, setColourId] = useState('0');
  const [flowerId, setFlowerId] = useState('0');
  const [cartId, setCartId] = useState('0');
  const [isNew, setIsNew] = useState(!props.match.params || !props.match.params.id);

  const { flowerInCartEntity, colours, flowers, carts, loading, updating } = props;

  const handleClose = () => {
    props.history.push('/flower-in-cart');
  };

  useEffect(() => {
    if (isNew) {
      props.reset();
    } else {
      props.getEntity(props.match.params.id);
    }

    props.getColours();
    props.getFlowers();
    props.getCarts();
  }, []);

  useEffect(() => {
    if (props.updateSuccess) {
      handleClose();
    }
  }, [props.updateSuccess]);

  const saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const entity = {
        ...flowerInCartEntity,
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
          <h2 id="flowershopApp.flowerInCart.home.createOrEditLabel">Create or edit a FlowerInCart</h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : flowerInCartEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="flower-in-cart-id">ID</Label>
                  <AvInput id="flower-in-cart-id" type="text" className="form-control" name="id" required readOnly />
                </AvGroup>
              ) : null}
              <AvGroup>
                <Label id="amountLabel" for="flower-in-cart-amount">
                  Amount
                </Label>
                <AvField id="flower-in-cart-amount" type="string" className="form-control" name="amount" />
              </AvGroup>
              <AvGroup>
                <Label for="flower-in-cart-colour">Colour</Label>
                <AvInput id="flower-in-cart-colour" type="select" className="form-control" name="colour.id">
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
                <Label for="flower-in-cart-flower">Flower</Label>
                <AvInput id="flower-in-cart-flower" type="select" className="form-control" name="flower.id">
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
                <Label for="flower-in-cart-cart">Cart</Label>
                <AvInput id="flower-in-cart-cart" type="select" className="form-control" name="cart.id">
                  <option value="" key="0" />
                  {carts
                    ? carts.map(otherEntity => (
                        <option value={otherEntity.id} key={otherEntity.id}>
                          {otherEntity.id}
                        </option>
                      ))
                    : null}
                </AvInput>
              </AvGroup>
              <Button tag={Link} id="cancel-save" to="/flower-in-cart" replace color="info">
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
  carts: storeState.cart.entities,
  flowerInCartEntity: storeState.flowerInCart.entity,
  loading: storeState.flowerInCart.loading,
  updating: storeState.flowerInCart.updating,
  updateSuccess: storeState.flowerInCart.updateSuccess
});

const mapDispatchToProps = {
  getColours,
  getFlowers,
  getCarts,
  getEntity,
  updateEntity,
  createEntity,
  reset
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(FlowerInCartUpdate);
