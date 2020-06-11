import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { ICrudGetAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './flower-in-cart.reducer';
import { IFlowerInCart } from 'app/shared/model/flower-in-cart.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IFlowerInCartDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const FlowerInCartDetail = (props: IFlowerInCartDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { flowerInCartEntity } = props;
  return (
    <Row>
      <Col md="8">
        <h2>
          FlowerInCart [<b>{flowerInCartEntity.id}</b>]
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="amount">Amount</span>
          </dt>
          <dd>{flowerInCartEntity.amount}</dd>
          <dt>Colour</dt>
          <dd>{flowerInCartEntity.colour ? flowerInCartEntity.colour.id : ''}</dd>
          <dt>Flower</dt>
          <dd>{flowerInCartEntity.flower ? flowerInCartEntity.flower.id : ''}</dd>
          <dt>Cart</dt>
          <dd>{flowerInCartEntity.cart ? flowerInCartEntity.cart.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/flower-in-cart" replace color="info">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/flower-in-cart/${flowerInCartEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ flowerInCart }: IRootState) => ({
  flowerInCartEntity: flowerInCart.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(FlowerInCartDetail);
