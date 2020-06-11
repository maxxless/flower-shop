import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { ICollection } from 'app/shared/model/collection.model';
import { getEntities as getCollections } from 'app/entities/collection/collection.reducer';
import { getEntity, updateEntity, createEntity, reset } from './packing.reducer';
import { IPacking } from 'app/shared/model/packing.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IPackingUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const PackingUpdate = (props: IPackingUpdateProps) => {
  const [collectionsId, setCollectionsId] = useState('0');
  const [isNew, setIsNew] = useState(!props.match.params || !props.match.params.id);

  const { packingEntity, collections, loading, updating } = props;

  const handleClose = () => {
    props.history.push('/packing');
  };

  useEffect(() => {
    if (!isNew) {
      props.getEntity(props.match.params.id);
    }

    props.getCollections();
  }, []);

  useEffect(() => {
    if (props.updateSuccess) {
      handleClose();
    }
  }, [props.updateSuccess]);

  const saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const entity = {
        ...packingEntity,
        ...values
      };

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
          <h2 id="flowershopApp.packing.home.createOrEditLabel">Create or edit a Packing</h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : packingEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="packing-id">ID</Label>
                  <AvInput id="packing-id" type="text" className="form-control" name="id" required readOnly />
                </AvGroup>
              ) : null}
              <AvGroup>
                <Label id="nameLabel" for="packing-name">
                  Name
                </Label>
                <AvField
                  id="packing-name"
                  type="text"
                  name="name"
                  validate={{
                    required: { value: true, errorMessage: 'This field is required.' }
                  }}
                />
              </AvGroup>
              <AvGroup>
                <Label id="materialLabel" for="packing-material">
                  Material
                </Label>
                <AvField id="packing-material" type="text" name="material" />
              </AvGroup>
              <AvGroup>
                <Label id="priceLabel" for="packing-price">
                  Price
                </Label>
                <AvField
                  id="packing-price"
                  type="string"
                  className="form-control"
                  name="price"
                  validate={{
                    required: { value: true, errorMessage: 'This field is required.' },
                    number: { value: true, errorMessage: 'This field should be a number.' }
                  }}
                />
              </AvGroup>
              <Button tag={Link} id="cancel-save" to="/packing" replace color="info">
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
  collections: storeState.collection.entities,
  packingEntity: storeState.packing.entity,
  loading: storeState.packing.loading,
  updating: storeState.packing.updating,
  updateSuccess: storeState.packing.updateSuccess
});

const mapDispatchToProps = {
  getCollections,
  getEntity,
  updateEntity,
  createEntity,
  reset
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(PackingUpdate);
