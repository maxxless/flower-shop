import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { ICrudGetAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './client-card.reducer';
import { IClientCard } from 'app/shared/model/client-card.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IClientCardDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const ClientCardDetail = (props: IClientCardDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { clientCardEntity } = props;
  return (
    <Row>
      <Col md="8">
        <h2>
          ClientCard [<b>{clientCardEntity.id}</b>]
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="name">Name</span>
          </dt>
          <dd>{clientCardEntity.name}</dd>
          <dt>
            <span id="description">Description</span>
          </dt>
          <dd>{clientCardEntity.description}</dd>
          <dt>
            <span id="type">Type</span>
          </dt>
          <dd>{clientCardEntity.type}</dd>
          <dt>
            <span id="bonusAmount">Bonus Amount</span>
          </dt>
          <dd>{clientCardEntity.bonusAmount}</dd>
          <dt>
            <span id="percentage">Percentage</span>
          </dt>
          <dd>{clientCardEntity.percentage}</dd>
          <dt>User</dt>
          <dd>{clientCardEntity.user ? clientCardEntity.user.firstName + ' ' + clientCardEntity.user.lastName : ''}</dd>
        </dl>
        <Button tag={Link} to="/client-card" replace color="info">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/client-card/${clientCardEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ clientCard }: IRootState) => ({
  clientCardEntity: clientCard.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(ClientCardDetail);
