import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { ICrudGetAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './delivery.reducer';
import { IDelivery } from 'app/shared/model/delivery.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IDeliveryDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const DeliveryDetail = (props: IDeliveryDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { deliveryEntity } = props;
  return (
    <Row>
      <Col md="8">
        <h2>
          Delivery [<b>{deliveryEntity.id}</b>]
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="address">Address</span>
          </dt>
          <dd>{deliveryEntity.address}</dd>
          <dt>
            <span id="postOfficeNumber">Post Office Number</span>
          </dt>
          <dd>{deliveryEntity.postOfficeNumber}</dd>
          <dt>
            <span id="price">Price</span>
          </dt>
          <dd>{deliveryEntity.price}</dd>
          <dt>
            <span id="type">Type</span>
          </dt>
          <dd>{deliveryEntity.type}</dd>
          <dt>Order</dt>
          <dd>{deliveryEntity.order ? deliveryEntity.order.id : ''}</dd>
          <dt>User</dt>
          <dd>{deliveryEntity.user ? deliveryEntity.user.firstName + ' ' + deliveryEntity.user.lastName : ''}</dd>
        </dl>
        <Button tag={Link} to="/delivery" replace color="info">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/delivery/${deliveryEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ delivery }: IRootState) => ({
  deliveryEntity: delivery.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(DeliveryDetail);
