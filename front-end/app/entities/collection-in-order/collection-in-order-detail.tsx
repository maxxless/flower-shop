import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { ICrudGetAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './collection-in-order.reducer';
import { ICollectionInOrder } from 'app/shared/model/collection-in-order.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface ICollectionInOrderDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const CollectionInOrderDetail = (props: ICollectionInOrderDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { collectionInOrderEntity } = props;
  return (
    <Row>
      <Col md="8">
        <h2>
          CollectionInOrder [<b>{collectionInOrderEntity.id}</b>]
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="amount">Amount</span>
          </dt>
          <dd>{collectionInOrderEntity.amount}</dd>
          <dt>Collection</dt>
          <dd>{collectionInOrderEntity.collection ? collectionInOrderEntity.collection.id : ''}</dd>
          <dt>Packing</dt>
          <dd>{collectionInOrderEntity.packing ? collectionInOrderEntity.packing.id : ''}</dd>
          <dt>Order</dt>
          <dd>{collectionInOrderEntity.order ? collectionInOrderEntity.order.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/collection-in-order" replace color="info">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/collection-in-order/${collectionInOrderEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ collectionInOrder }: IRootState) => ({
  collectionInOrderEntity: collectionInOrder.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(CollectionInOrderDetail);
