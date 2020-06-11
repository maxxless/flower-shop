import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { ICrudGetAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './flower-in-order.reducer';
import { IFlowerInOrder } from 'app/shared/model/flower-in-order.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IFlowerInOrderDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const FlowerInOrderDetail = (props: IFlowerInOrderDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { flowerInOrderEntity } = props;
  return (
    <Row>
      <Col md="8">
        <h2>
          FlowerInOrder [<b>{flowerInOrderEntity.id}</b>]
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="amount">Amount</span>
          </dt>
          <dd>{flowerInOrderEntity.amount}</dd>
          <dt>Colour</dt>
          <dd>{flowerInOrderEntity.colour ? flowerInOrderEntity.colour.id : ''}</dd>
          <dt>Flower</dt>
          <dd>{flowerInOrderEntity.flower ? flowerInOrderEntity.flower.id : ''}</dd>
          <dt>Order</dt>
          <dd>{flowerInOrderEntity.order ? flowerInOrderEntity.order.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/flower-in-order" replace color="info">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/flower-in-order/${flowerInOrderEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ flowerInOrder }: IRootState) => ({
  flowerInOrderEntity: flowerInOrder.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(FlowerInOrderDetail);
