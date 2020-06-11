import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, InputGroup, Col, Row, Table } from 'reactstrap';
import { AvForm, AvGroup, AvInput } from 'availity-reactstrap-validation';
import { ICrudSearchAction, ICrudGetAllAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getSearchEntities, getEntities } from './flower-in-cart.reducer';
import { IFlowerInCart } from 'app/shared/model/flower-in-cart.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IFlowerInCartProps extends StateProps, DispatchProps, RouteComponentProps<{ url: string }> {}

export const FlowerInCart = (props: IFlowerInCartProps) => {
  const [search, setSearch] = useState('');

  useEffect(() => {
    props.getEntities();
  }, []);

  const startSearching = () => {
    if (search) {
      props.getSearchEntities(search);
    }
  };

  const clear = () => {
    setSearch('');
    props.getEntities();
  };

  const handleSearch = event => setSearch(event.target.value);

  const { flowerInCartList, match, loading } = props;
  return (
    <div>
      <h2 id="flower-in-cart-heading">
        Flower In Carts
        <Link to={`${match.url}/new`} className="btn btn-primary float-right jh-create-entity" id="jh-create-entity">
          <FontAwesomeIcon icon="plus" />
          &nbsp; Create new Flower In Cart
        </Link>
      </h2>
      <Row>
        <Col sm="12">
          <AvForm onSubmit={startSearching}>
            <AvGroup>
              <InputGroup>
                <AvInput type="text" name="search" value={search} onChange={handleSearch} placeholder="Search" />
                <Button className="input-group-addon">
                  <FontAwesomeIcon icon="search" />
                </Button>
                <Button type="reset" className="input-group-addon" onClick={clear}>
                  <FontAwesomeIcon icon="trash" />
                </Button>
              </InputGroup>
            </AvGroup>
          </AvForm>
        </Col>
      </Row>
      <div className="table-responsive">
        {flowerInCartList && flowerInCartList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>ID</th>
                <th>Amount</th>
                <th>Colour</th>
                <th>Flower</th>
                <th>Cart</th>
                <th />
              </tr>
            </thead>
            <tbody>
              {flowerInCartList.map((flowerInCart, i) => (
                <tr key={`entity-${i}`}>
                  <td>
                    <Button tag={Link} to={`${match.url}/${flowerInCart.id}`} color="link" size="sm">
                      {flowerInCart.id}
                    </Button>
                  </td>
                  <td>{flowerInCart.amount}</td>
                  <td>{flowerInCart.colour ? <Link to={`colour/${flowerInCart.colour.id}`}>{flowerInCart.colour.id}</Link> : ''}</td>
                  <td>{flowerInCart.flower ? <Link to={`flower/${flowerInCart.flower.id}`}>{flowerInCart.flower.id}</Link> : ''}</td>
                  <td>{flowerInCart.cart ? <Link to={`cart/${flowerInCart.cart.id}`}>{flowerInCart.cart.id}</Link> : ''}</td>
                  <td className="text-right">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`${match.url}/${flowerInCart.id}`} color="info" size="sm">
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">View</span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${flowerInCart.id}/edit`} color="primary" size="sm">
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${flowerInCart.id}/delete`} color="danger" size="sm">
                        <FontAwesomeIcon icon="trash" /> <span className="d-none d-md-inline">Delete</span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && <div className="alert alert-warning">No Flower In Carts found</div>
        )}
      </div>
    </div>
  );
};

const mapStateToProps = ({ flowerInCart }: IRootState) => ({
  flowerInCartList: flowerInCart.entities,
  loading: flowerInCart.loading
});

const mapDispatchToProps = {
  getSearchEntities,
  getEntities
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(FlowerInCart);
