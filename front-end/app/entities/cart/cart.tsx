import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, InputGroup, Col, Row as h2, Table } from 'reactstrap';
import { AvForm, AvGroup, AvInput } from 'availity-reactstrap-validation';
import { ICrudSearchAction, ICrudGetAllAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getSearchEntities, getEntities } from './cart.reducer';
import { ICart } from 'app/shared/model/cart.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { getAccountDetails } from 'app/shared/util/api';

export interface ICartProps extends StateProps, DispatchProps, RouteComponentProps<{ url: string }> { }

export const Cart = (props: ICartProps) => {
  const [search, setSearch] = useState('');
  const [cartDetails, setCartDetails] = useState({ collectionDetails: null, flowerDetails: null })

  useEffect(() => {
    props.getEntities();

    getAccountDetails().then((res) => setCartDetails(res.cart));
  }, []);

  const { cartList, match, loading } = props;
  const { collectionDetails, flowerDetails } = cartDetails;

  return (
    <div>
      <h2>
        Final price: {cartDetails.finalPrice}. (Price without discount {cartDetails.totalPriceWithoutDiscount})
        <Link to={`${match.url}/new`} className="btn btn-primary float-right jh-create-entity" id="jh-create-entity">
          <FontAwesomeIcon icon="plus" />
          &nbsp; Create Order
        </Link>
      </h2>
      <h2>
        Total card discount {cartDetails.cardDiscount}.
      </h2>
      <h2>
        Total bonus discount {cartDetails.bonusDiscount}.
      </h2>
      <h2>
        Collections
      </h2>
      <div className="table-responsive">
        {collectionDetails && collectionDetails.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>Total amount</th>
                <th>Name</th>
                <th>Price</th>
                <th>Image</th>
                <th>Category</th>
                <th />
              </tr>
            </thead>
            <tbody>
              {collectionDetails.map(({ id, amount, collection }, i) => (
                <tr key={`entity-${i}`}>
                  <td>{amount}</td>
                  <td>{collection.name}</td>
                  <td>{collection.price}</td>
                  <td>
                    <img
                      src={`data:${collection.imageContentType};base64,${collection.image}`}
                      style={{ maxHeight: '60px' }}
                    />
                  </td>
                  <td>{collection.category.name}</td>
                  <td className="text-right">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`${match.url}/${id}/delete`} color="danger" size="sm">
                        <FontAwesomeIcon icon="trash" /> <span className="d-none d-md-inline">Delete</span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
            !loading && <div className="alert alert-warning">You haven't added any collections yet</div>
          )}
      </div>
      <h2>
        Flowers
      </h2>
      <div className="table-responsive">
        {flowerDetails && flowerDetails.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>Total amount</th>
                <th>Colour</th>
                <th>Name</th>
                <th>Price</th>
                <th>Image</th>
                <th />
              </tr>
            </thead>
            <tbody>
              {flowerDetails.map(({ id, amount, colour, flower }, i) => (
                <tr key={`entity-${i}`}>
                  <td>{amount}</td>
                  <td>{colour.name}</td>
                  <td>{flower.name}</td>
                  <td>{flower.price}</td>
                  <td>
                    <img
                      src={`data:${flower.imageContentType};base64,${flower.image}`}
                      style={{ maxHeight: '60px' }}
                    />
                  </td>
                  <td className="text-right">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`${match.url}/${id}/delete`} color="danger" size="sm">
                        <FontAwesomeIcon icon="trash" /> <span className="d-none d-md-inline">Delete</span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
            !loading && <div className="alert alert-warning">You haven't added any flowers yet</div>
          )}
      </div>
    </div>
  );
};

const mapStateToProps = ({ cart }: IRootState) => ({
  cartList: cart.entities,
  loading: cart.loading
});

const mapDispatchToProps = {
  getSearchEntities,
  getEntities
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(Cart);
