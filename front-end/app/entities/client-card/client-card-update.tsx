import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { IUser } from 'app/shared/model/user.model';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { getEntity, updateEntity, createEntity, reset } from './client-card.reducer';
import { IClientCard } from 'app/shared/model/client-card.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IClientCardUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const ClientCardUpdate = (props: IClientCardUpdateProps) => {
  const [userId, setUserId] = useState('0');
  const [isNew, setIsNew] = useState(!props.match.params || !props.match.params.id);

  const { clientCardEntity, users, loading, updating } = props;

  const handleClose = () => {
    props.history.push('/client-card');
  };

  useEffect(() => {
    if (isNew) {
      props.reset();
    } else {
      props.getEntity(props.match.params.id);
    }

    props.getUsers();
  }, []);

  useEffect(() => {
    if (props.updateSuccess) {
      handleClose();
    }
  }, [props.updateSuccess]);

  const saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const entity = {
        ...clientCardEntity,
        ...values
      };
      entity.user = users[values.user];

      if (isNew) {
        props.createEntity(entity);
      } else {
        props.updateEntity(entity);
      }
    }
  };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="flowershopApp.clientCard.home.createOrEditLabel">Create or edit a ClientCard</h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : clientCardEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="client-card-id">ID</Label>
                  <AvInput id="client-card-id" type="text" className="form-control" name="id" required readOnly />
                </AvGroup>
              ) : null}
              <AvGroup>
                <Label id="nameLabel" for="client-card-name">
                  Name
                </Label>
                <AvField
                  id="client-card-name"
                  type="text"
                  name="name"
                  validate={{
                    required: { value: true, errorMessage: 'This field is required.' }
                  }}
                />
              </AvGroup>
              <AvGroup>
                <Label id="descriptionLabel" for="client-card-description">
                  Description
                </Label>
                <AvField id="client-card-description" type="text" name="description" />
              </AvGroup>
              <AvGroup>
                <Label id="typeLabel" for="client-card-type">
                  Type
                </Label>
                <AvInput
                  id="client-card-type"
                  type="select"
                  className="form-control"
                  name="type"
                  value={(!isNew && clientCardEntity.type) || 'BONUS'}
                >
                  <option value="BONUS">BONUS</option>
                  <option value="SOCIAL">SOCIAL</option>
                  <option value="GOLD">GOLD</option>
                </AvInput>
              </AvGroup>
              <AvGroup>
                <Label id="bonusAmountLabel" for="client-card-bonusAmount">
                  Bonus Amount
                </Label>
                <AvField id="client-card-bonusAmount" type="string" className="form-control" name="bonusAmount" />
              </AvGroup>
              <AvGroup>
                <Label id="percentageLabel" for="client-card-percentage">
                  Percentage
                </Label>
                <AvField id="client-card-percentage" type="string" className="form-control" name="percentage" />
              </AvGroup>
              <AvGroup>
                <Label for="client-card-user">User</Label>
                <AvInput id="client-card-user" type="select" className="form-control" name="user">
                  <option value="" key="0" />
                  {users
                    ? users.map((otherEntity, index) => (
                        <option value={index} key={otherEntity.id}>
                          {otherEntity.firstName + ' ' + otherEntity.lastName}
                        </option>
                      ))
                    : null}
                </AvInput>
              </AvGroup>
              <Button tag={Link} id="cancel-save" to="/client-card" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">Back</span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp; Save
              </Button>
            </AvForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

const mapStateToProps = (storeState: IRootState) => ({
  users: storeState.userManagement.users,
  clientCardEntity: storeState.clientCard.entity,
  loading: storeState.clientCard.loading,
  updating: storeState.clientCard.updating,
  updateSuccess: storeState.clientCard.updateSuccess
});

const mapDispatchToProps = {
  getUsers,
  getEntity,
  updateEntity,
  createEntity,
  reset
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(ClientCardUpdate);
