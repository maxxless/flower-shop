import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { IUser } from 'app/shared/model/user.model';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { getEntity, updateEntity, createEntity, reset } from './cart.reducer';
import { ICart } from 'app/shared/model/cart.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { makeOrder, getPackings } from 'app/shared/util/api';

export interface ICartUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> { }

interface iOrderInfo {
  address: String,
  deliveryType: String,
  packingId: String,
  postOfficeNumber?: String,
}

const DELIVERY_TYPES = {
  POST_OFFICE: "POST_OFFICE",
  COURIER: "COURIER",
  SELF_PICK_UP: "SELF_PICK_UP",
}

export const CartUpdate = (props: ICartUpdateProps) => {
  const [userId, setUserId] = useState('0');
  const [isNew, setIsNew] = useState(!props.match.params || !props.match.params.id);

  const { cartEntity, loading, updating } = props;

  const [packings, setPackings] = useState(null)
  const [orderInfo, setOrderInfo] = useState<iOrderInfo>({
    address: "",
    deliveryType: "",
    packingId: "",
  })

  const handleClose = () => {
    props.history.push('/cart');
  };

  useEffect(() => {
    if (isNew) {
      props.reset();
    } else {
      props.getEntity(props.match.params.id);
    }

    props.getUsers();

    getPackings().then(res => setPackings(res))
  }, []);

  useEffect(() => {
    if (props.updateSuccess) {
      handleClose();
    }
  }, [props.updateSuccess]);

  const handleChange = e => {
    const { name, value } = e.target;

    setOrderInfo(prev => ({ ...prev, [name]: value }))
  }

  const handleMakeOrder = () => {
    makeOrder(orderInfo).then(() => window.location.reload(false))
  }

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="flowershopApp.cart.home.createOrEditLabel">Make new order</h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
              <AvForm model={isNew ? {} : cartEntity}>
                <AvGroup>
                  <Label for="cart-cardDiscount">
                    Delivery Type
                </Label>
                  <AvInput type="select" className="form-control" name="deliveryType" onChange={handleChange} >
                    <option value="" key="0" />
                    {DELIVERY_TYPES
                      ? Object.keys(DELIVERY_TYPES).map((type, index) => (
                        <option value={type} key={type}>
                          {type}
                        </option>
                      ))
                      : null}
                  </AvInput>
                </AvGroup>
                {[DELIVERY_TYPES.COURIER, DELIVERY_TYPES.POST_OFFICE].includes(orderInfo.deliveryType) && <AvGroup>
                  <Label id="totalPriceWithoutDiscountLabel" for="cart-totalPriceWithoutDiscount">
                    Address
                <AvField id="cart-totalPriceWithoutDiscount" type="string" className="form-control" name="address" onChange={handleChange} />
                  </Label>
                </AvGroup>
                }
                {orderInfo.deliveryType === DELIVERY_TYPES.POST_OFFICE && <AvGroup>
                  <Label id="bonusDiscountLabel" for="cart-bonusDiscount">
                    Post Office number
                </Label>
                  <AvField type="number" className="form-control" name="postOfficeNumber" onChange={handleChange} />
                </AvGroup>}
                <AvGroup>
                  <Label for="cart-user">Packings</Label>
                  <AvInput type="select" className="form-control" name="packingId" onChange={handleChange} >
                    <option value="" key="0" />
                    {packings
                      ? packings.map((pack, index) => (
                        <option value={pack.id} key={pack.id}>
                          {pack.name}
                        </option>
                      ))
                      : null}
                  </AvInput>
                </AvGroup>
                <Button tag={Link} id="cancel-save" to="/cart" replace color="info">
                  <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">Back</span>
                </Button>
              &nbsp;
                <Button color="primary" id="save-entity" type="submit" disabled={updating} onClick={handleMakeOrder}>
                  <FontAwesomeIcon icon="save" />
                &nbsp; Make order
              </Button>
              </AvForm>
            )}
        </Col>
      </Row>
    </div>
  );
};

const mapStateToProps = (storeState: IRootState) => ({
  users: storeState.userManagement.users,
  cartEntity: storeState.cart.entity,
  loading: storeState.cart.loading,
  updating: storeState.cart.updating,
  updateSuccess: storeState.cart.updateSuccess
});

const mapDispatchToProps = {
  getUsers,
  getEntity,
  updateEntity,
  createEntity,
  reset
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(CartUpdate);
