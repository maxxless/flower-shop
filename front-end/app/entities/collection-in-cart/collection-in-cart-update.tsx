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
import { ICart } from 'app/shared/model/cart.model';
import { getEntities as getCarts } from 'app/entities/cart/cart.reducer';
import { getEntity, updateEntity, createEntity, reset } from './collection-in-cart.reducer';
import { ICollectionInCart } from 'app/shared/model/collection-in-cart.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface ICollectionInCartUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const CollectionInCartUpdate = (props: ICollectionInCartUpdateProps) => {
  const [collectionId, setCollectionId] = useState('0');
  const [packingId, setPackingId] = useState('0');
  const [cartId, setCartId] = useState('0');
  const [isNew, setIsNew] = useState(!props.match.params || !props.match.params.id);

  const { collectionInCartEntity, collections, packings, carts, loading, updating } = props;

  const handleClose = () => {
    props.history.push('/collection-in-cart');
  };

  useEffect(() => {
    if (isNew) {
      props.reset();
    } else {
      props.getEntity(props.match.params.id);
    }

    props.getCollections();
    props.getPackings();
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
        ...collectionInCartEntity,
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
          <h2 id="flowershopApp.collectionInCart.home.createOrEditLabel">Create or edit a CollectionInCart</h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : collectionInCartEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="collection-in-cart-id">ID</Label>
                  <AvInput id="collection-in-cart-id" type="text" className="form-control" name="id" required readOnly />
                </AvGroup>
              ) : null}
              <AvGroup>
                <Label id="amountLabel" for="collection-in-cart-amount">
                  Amount
                </Label>
                <AvField id="collection-in-cart-amount" type="string" className="form-control" name="amount" />
              </AvGroup>
              <AvGroup>
                <Label for="collection-in-cart-collection">Collection</Label>
                <AvInput id="collection-in-cart-collection" type="select" className="form-control" name="collection.id">
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
                <Label for="collection-in-cart-packing">Packing</Label>
                <AvInput id="collection-in-cart-packing" type="select" className="form-control" name="packing.id">
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
                <Label for="collection-in-cart-cart">Cart</Label>
                <AvInput id="collection-in-cart-cart" type="select" className="form-control" name="cart.id">
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
              <Button tag={Link} id="cancel-save" to="/collection-in-cart" replace color="info">
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
  carts: storeState.cart.entities,
  collectionInCartEntity: storeState.collectionInCart.entity,
  loading: storeState.collectionInCart.loading,
  updating: storeState.collectionInCart.updating,
  updateSuccess: storeState.collectionInCart.updateSuccess
});

const mapDispatchToProps = {
  getCollections,
  getPackings,
  getCarts,
  getEntity,
  updateEntity,
  createEntity,
  reset
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(CollectionInCartUpdate);
