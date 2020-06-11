import React, { useEffect, useState } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { ICrudGetAction, openFile, byteSize } from 'react-jhipster';
import { AvInput } from 'availity-reactstrap-validation';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './collection.reducer';
import { ICollection } from 'app/shared/model/collection.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { addCollectionToCart } from 'app/shared/util/api';

export interface ICollectionDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> { }

export const CollectionDetail = (props: ICollectionDetailProps) => {
  const [amount, setAmount] = useState(0);
  const [packingId, setPackingId] = useState(null);

  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const handleAmountChange = (e) => setAmount(+e.target.value);

  const handleAddToCart = () => {
    const { collectionEntity } = props;
    const body = {
      amount,
      packingId
    }

    return addCollectionToCart(collectionEntity.id, body);
  }

  const { collectionEntity } = props;
  return (
    <Row>
      <Col md="8">
        <h2>
          Collection [<b>{collectionEntity.id}</b>]
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="name">Name</span>
          </dt>
          <dd>{collectionEntity.name}</dd>
          <dt>
            <span id="description">Description</span>
          </dt>
          <dd>{collectionEntity.description}</dd>
          <dt>
            <span id="price">Price</span>
          </dt>
          <dd>{collectionEntity.price}</dd>
          <dt>
            <span id="image">Image</span>
          </dt>
          <dd>
            {collectionEntity.image ? (
              <div>
                <a onClick={openFile(collectionEntity.imageContentType, collectionEntity.image)}>
                  <img src={`data:${collectionEntity.imageContentType};base64,${collectionEntity.image}`} style={{ maxHeight: '30px' }} />
                </a>
                <span>
                  {collectionEntity.imageContentType}, {byteSize(collectionEntity.image)}
                </span>
              </div>
            ) : null}
          </dd>
          <dt>Available Packings</dt>
          <dd style={{ display: 'flex', flexDirection: "column" }}>
            {collectionEntity.availablePackings
              ? collectionEntity.availablePackings.map((val, i) => (
                <label key={val.id} style={{ cursor: "pointer" }}>
                  <input
                    style={{ marginRight: 10 }}
                    type="radio"
                    name={val.name}
                    value={val.name}
                    checked={packingId === val.id}
                    onClick={() => setPackingId(val.id)}
                  />
                  {val.name}
                </label>
              ))
              : null}
          </dd>
          <dt>Amount</dt>
          <dd>
            <input type="number" onChange={handleAmountChange} />
          </dd>
          <dt>Flowers</dt>
          <dd>
            {collectionEntity.flowers
              ? collectionEntity.flowers.map((val, i) => (
                <span key={val.name}>
                  <a>{val.name}</a>
                  {i === collectionEntity.flowers.length - 1 ? '' : ', '}
                </span>
              ))
              : null}
          </dd>
          <dt>Category</dt>
          <dd>{collectionEntity.category ? collectionEntity.category.name : ''}</dd>
        </dl>
        <Button tag={Link} to="/collection" replace color="info">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button onClick={handleAddToCart} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Add to cart</span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ collection }: IRootState) => ({
  collectionEntity: collection.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(CollectionDetail);
