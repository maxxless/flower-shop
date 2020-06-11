import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
import { ICrudGetAction, ICrudGetAllAction, setFileData, openFile, byteSize, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { IColour } from 'app/shared/model/colour.model';
import { getEntities as getColours } from 'app/entities/colour/colour.reducer';
import { ICollection } from 'app/shared/model/collection.model';
import { getEntities as getCollections } from 'app/entities/collection/collection.reducer';
import { getEntity, updateEntity, createEntity, setBlob, reset } from './flower.reducer';
import { IFlower } from 'app/shared/model/flower.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IFlowerUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const FlowerUpdate = (props: IFlowerUpdateProps) => {
  const [idsavailableColours, setIdsavailableColours] = useState([]);
  const [collectionsInId, setCollectionsInId] = useState('0');
  const [isNew, setIsNew] = useState(!props.match.params || !props.match.params.id);

  const { flowerEntity, colours, collections, loading, updating } = props;

  const { image, imageContentType } = flowerEntity;

  const handleClose = () => {
    props.history.push('/flower');
  };

  useEffect(() => {
    if (!isNew) {
      props.getEntity(props.match.params.id);
    }

    props.getColours();
    props.getCollections();
  }, []);

  const onBlobChange = (isAnImage, name) => event => {
    setFileData(event, (contentType, data) => props.setBlob(name, data, contentType), isAnImage);
  };

  const clearBlob = name => () => {
    props.setBlob(name, undefined, undefined);
  };

  useEffect(() => {
    if (props.updateSuccess) {
      handleClose();
    }
  }, [props.updateSuccess]);

  const saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const entity = {
        ...flowerEntity,
        ...values,
        availableColours: mapIdList(values.availableColours)
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
          <h2 id="flowershopApp.flower.home.createOrEditLabel">Create or edit a Flower</h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : flowerEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="flower-id">ID</Label>
                  <AvInput id="flower-id" type="text" className="form-control" name="id" required readOnly />
                </AvGroup>
              ) : null}
              <AvGroup>
                <Label id="nameLabel" for="flower-name">
                  Name
                </Label>
                <AvField
                  id="flower-name"
                  type="text"
                  name="name"
                  validate={{
                    required: { value: true, errorMessage: 'This field is required.' }
                  }}
                />
              </AvGroup>
              <AvGroup>
                <Label id="descriptionLabel" for="flower-description">
                  Description
                </Label>
                <AvField id="flower-description" type="text" name="description" />
              </AvGroup>
              <AvGroup>
                <Label id="priceLabel" for="flower-price">
                  Price
                </Label>
                <AvField
                  id="flower-price"
                  type="string"
                  className="form-control"
                  name="price"
                  validate={{
                    required: { value: true, errorMessage: 'This field is required.' },
                    number: { value: true, errorMessage: 'This field should be a number.' }
                  }}
                />
              </AvGroup>
              <AvGroup>
                <AvGroup>
                  <Label id="imageLabel" for="image">
                    Image
                  </Label>
                  <br />
                  {image ? (
                    <div>
                      <a onClick={openFile(imageContentType, image)}>
                        <img src={`data:${imageContentType};base64,${image}`} style={{ maxHeight: '100px' }} />
                      </a>
                      <br />
                      <Row>
                        <Col md="11">
                          <span>
                            {imageContentType}, {byteSize(image)}
                          </span>
                        </Col>
                        <Col md="1">
                          <Button color="danger" onClick={clearBlob('image')}>
                            <FontAwesomeIcon icon="times-circle" />
                          </Button>
                        </Col>
                      </Row>
                    </div>
                  ) : null}
                  <input id="file_image" type="file" onChange={onBlobChange(true, 'image')} accept="image/*" />
                  <AvInput type="hidden" name="image" value={image} />
                </AvGroup>
              </AvGroup>
              <AvGroup>
                <Label for="flower-availableColours">Available Colours</Label>
                <AvInput
                  id="flower-availableColours"
                  type="select"
                  multiple
                  className="form-control"
                  name="availableColours"
                  value={flowerEntity.availableColours && flowerEntity.availableColours.map(e => e.id)}
                >
                  <option value="" key="0" />
                  {colours
                    ? colours.map(otherEntity => (
                        <option value={otherEntity.id} key={otherEntity.id}>
                          {otherEntity.name}
                        </option>
                      ))
                    : null}
                </AvInput>
              </AvGroup>
              <Button tag={Link} id="cancel-save" to="/flower" replace color="info">
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
  colours: storeState.colour.entities,
  collections: storeState.collection.entities,
  flowerEntity: storeState.flower.entity,
  loading: storeState.flower.loading,
  updating: storeState.flower.updating,
  updateSuccess: storeState.flower.updateSuccess
});

const mapDispatchToProps = {
  getColours,
  getCollections,
  getEntity,
  updateEntity,
  setBlob,
  createEntity,
  reset
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(FlowerUpdate);
