import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
import { ICrudGetAction, ICrudGetAllAction, setFileData, openFile, byteSize, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { IPacking } from 'app/shared/model/packing.model';
import { getEntities as getPackings } from 'app/entities/packing/packing.reducer';
import { IFlower } from 'app/shared/model/flower.model';
import { getEntities as getFlowers } from 'app/entities/flower/flower.reducer';
import { ICategory } from 'app/shared/model/category.model';
import { getEntities as getCategories } from 'app/entities/category/category.reducer';
import { getEntity, updateEntity, createEntity, setBlob, reset } from './collection.reducer';
import { ICollection } from 'app/shared/model/collection.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface ICollectionUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const CollectionUpdate = (props: ICollectionUpdateProps) => {
  const [idsavailablePackings, setIdsavailablePackings] = useState([]);
  const [idsflowers, setIdsflowers] = useState([]);
  const [categoryId, setCategoryId] = useState('0');
  const [isNew, setIsNew] = useState(!props.match.params || !props.match.params.id);

  const { collectionEntity, packings, flowers, categories, loading, updating } = props;

  const { image, imageContentType } = collectionEntity;

  const handleClose = () => {
    props.history.push('/collection');
  };

  useEffect(() => {
    if (!isNew) {
      props.getEntity(props.match.params.id);
    }

    props.getPackings();
    props.getFlowers();
    props.getCategories();
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
        ...collectionEntity,
        ...values,
        availablePackings: mapIdList(values.availablePackings),
        flowers: mapIdList(values.flowers)
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
          <h2 id="flowershopApp.collection.home.createOrEditLabel">Create or edit a Collection</h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : collectionEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="collection-id">ID</Label>
                  <AvInput id="collection-id" type="text" className="form-control" name="id" required readOnly />
                </AvGroup>
              ) : null}
              <AvGroup>
                <Label id="nameLabel" for="collection-name">
                  Name
                </Label>
                <AvField
                  id="collection-name"
                  type="text"
                  name="name"
                  validate={{
                    required: { value: true, errorMessage: 'This field is required.' }
                  }}
                />
              </AvGroup>
              <AvGroup>
                <Label id="descriptionLabel" for="collection-description">
                  Description
                </Label>
                <AvField id="collection-description" type="text" name="description" />
              </AvGroup>
              <AvGroup>
                <Label id="priceLabel" for="collection-price">
                  Price
                </Label>
                <AvField
                  id="collection-price"
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
                <Label for="collection-availablePackings">Available Packings</Label>
                <AvInput
                  id="collection-availablePackings"
                  type="select"
                  multiple
                  className="form-control"
                  name="availablePackings"
                  value={collectionEntity.availablePackings && collectionEntity.availablePackings.map(e => e.id)}
                >
                  <option value="" key="0" />
                  {packings
                    ? packings.map(otherEntity => (
                        <option value={otherEntity.id} key={otherEntity.id}>
                          {otherEntity.name}
                        </option>
                      ))
                    : null}
                </AvInput>
              </AvGroup>
              <AvGroup>
                <Label for="collection-flowers">Flowers</Label>
                <AvInput
                  id="collection-flowers"
                  type="select"
                  multiple
                  className="form-control"
                  name="flowers"
                  value={collectionEntity.flowers && collectionEntity.flowers.map(e => e.id)}
                >
                  <option value="" key="0" />
                  {flowers
                    ? flowers.map(otherEntity => (
                        <option value={otherEntity.id} key={otherEntity.id}>
                          {otherEntity.name}
                        </option>
                      ))
                    : null}
                </AvInput>
              </AvGroup>
              <AvGroup>
                <Label for="collection-category">Category</Label>
                <AvInput id="collection-category" type="select" className="form-control" name="category.id">
                  <option value="" key="0" />
                  {categories
                    ? categories.map(otherEntity => (
                        <option value={otherEntity.id} key={otherEntity.id}>
                          {otherEntity.name}
                        </option>
                      ))
                    : null}
                </AvInput>
              </AvGroup>
              <Button tag={Link} id="cancel-save" to="/collection" replace color="info">
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
  packings: storeState.packing.entities,
  flowers: storeState.flower.entities,
  categories: storeState.category.entities,
  collectionEntity: storeState.collection.entity,
  loading: storeState.collection.loading,
  updating: storeState.collection.updating,
  updateSuccess: storeState.collection.updateSuccess
});

const mapDispatchToProps = {
  getPackings,
  getFlowers,
  getCategories,
  getEntity,
  updateEntity,
  setBlob,
  createEntity,
  reset
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(CollectionUpdate);
