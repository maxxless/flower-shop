import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { ICrudGetAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './packing.reducer';
import { IPacking } from 'app/shared/model/packing.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IPackingDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const PackingDetail = (props: IPackingDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { packingEntity } = props;
  return (
    <Row>
      <Col md="8">
        <h2>
          Packing [<b>{packingEntity.id}</b>]
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="name">Name</span>
          </dt>
          <dd>{packingEntity.name}</dd>
          <dt>
            <span id="material">Material</span>
          </dt>
          <dd>{packingEntity.material}</dd>
          <dt>
            <span id="price">Price</span>
          </dt>
          <dd>{packingEntity.price}</dd>
        </dl>
        <Button tag={Link} to="/packing" replace color="info">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/packing/${packingEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Add to Cart</span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ packing }: IRootState) => ({
  packingEntity: packing.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(PackingDetail);
