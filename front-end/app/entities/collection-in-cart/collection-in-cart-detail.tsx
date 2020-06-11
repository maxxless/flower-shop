import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { ICrudGetAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './collection-in-cart.reducer';
import { ICollectionInCart } from 'app/shared/model/collection-in-cart.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface ICollectionInCartDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const CollectionInCartDetail = (props: ICollectionInCartDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { collectionInCartEntity } = props;
  return (
    <Row>
      <Col md="8">
        <h2>
          CollectionInCart [<b>{collectionInCartEntity.id}</b>]
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="amount">Amount</span>
          </dt>
          <dd>{collectionInCartEntity.amount}</dd>
          <dt>Collection</dt>
          <dd>{collectionInCartEntity.collection ? collectionInCartEntity.collection.id : ''}</dd>
          <dt>Packing</dt>
          <dd>{collectionInCartEntity.packing ? collectionInCartEntity.packing.id : ''}</dd>
          <dt>Cart</dt>
          <dd>{collectionInCartEntity.cart ? collectionInCartEntity.cart.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/collection-in-cart" replace color="info">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/collection-in-cart/${collectionInCartEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ collectionInCart }: IRootState) => ({
  collectionInCartEntity: collectionInCart.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(CollectionInCartDetail);
