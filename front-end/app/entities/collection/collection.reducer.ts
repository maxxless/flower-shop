import axios from 'axios';
import {
  ICrudSearchAction,
  parseHeaderForLinks,
  loadMoreDataWhenScrolled,
  ICrudGetAction,
  ICrudGetAllAction,
  ICrudPutAction,
  ICrudDeleteAction
} from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { ICollection, defaultValue } from 'app/shared/model/collection.model';

export const ACTION_TYPES = {
  SEARCH_COLLECTIONS: 'collection/SEARCH_COLLECTIONS',
  FETCH_COLLECTION_LIST: 'collection/FETCH_COLLECTION_LIST',
  FETCH_COLLECTION: 'collection/FETCH_COLLECTION',
  CREATE_COLLECTION: 'collection/CREATE_COLLECTION',
  UPDATE_COLLECTION: 'collection/UPDATE_COLLECTION',
  DELETE_COLLECTION: 'collection/DELETE_COLLECTION',
  SET_BLOB: 'collection/SET_BLOB',
  RESET: 'collection/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<ICollection>,
  entity: defaultValue,
  links: { next: 0 },
  updating: false,
  totalItems: 0,
  updateSuccess: false
};

export type CollectionState = Readonly<typeof initialState>;

// Reducer

export default (state: CollectionState = initialState, action): CollectionState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_COLLECTIONS):
    case REQUEST(ACTION_TYPES.FETCH_COLLECTION_LIST):
    case REQUEST(ACTION_TYPES.FETCH_COLLECTION):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_COLLECTION):
    case REQUEST(ACTION_TYPES.UPDATE_COLLECTION):
    case REQUEST(ACTION_TYPES.DELETE_COLLECTION):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.SEARCH_COLLECTIONS):
    case FAILURE(ACTION_TYPES.FETCH_COLLECTION_LIST):
    case FAILURE(ACTION_TYPES.FETCH_COLLECTION):
    case FAILURE(ACTION_TYPES.CREATE_COLLECTION):
    case FAILURE(ACTION_TYPES.UPDATE_COLLECTION):
    case FAILURE(ACTION_TYPES.DELETE_COLLECTION):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.SEARCH_COLLECTIONS):
    case SUCCESS(ACTION_TYPES.FETCH_COLLECTION_LIST): {
      const links = parseHeaderForLinks(action.payload.headers.link);

      return {
        ...state,
        loading: false,
        links,
        entities: loadMoreDataWhenScrolled(state.entities, action.payload.data, links),
        totalItems: parseInt(action.payload.headers['x-total-count'], 10)
      };
    }
    case SUCCESS(ACTION_TYPES.FETCH_COLLECTION):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_COLLECTION):
    case SUCCESS(ACTION_TYPES.UPDATE_COLLECTION):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_COLLECTION):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: {}
      };
    case ACTION_TYPES.SET_BLOB: {
      const { name, data, contentType } = action.payload;
      return {
        ...state,
        entity: {
          ...state.entity,
          [name]: data,
          [name + 'ContentType']: contentType
        }
      };
    }
    case ACTION_TYPES.RESET:
      return {
        ...initialState
      };
    default:
      return state;
  }
};

const apiUrl = 'api/collections';
const apiSearchUrl = 'api/_search/collections';

// Actions

export const getSearchEntities: ICrudSearchAction<ICollection> = (query, page, size, sort) => ({
  type: ACTION_TYPES.SEARCH_COLLECTIONS,
  payload: axios.get<ICollection>(`${apiSearchUrl}?query=${query}${sort ? `&page=${page}&size=${size}&sort=${sort}` : ''}`)
});

export const getEntities: ICrudGetAllAction<ICollection> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_COLLECTION_LIST,
    payload: axios.get<ICollection>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`)
  };
};

export const getEntity: ICrudGetAction<ICollection> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_COLLECTION,
    payload: axios.get<ICollection>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<ICollection> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_COLLECTION,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  return result;
};

export const updateEntity: ICrudPutAction<ICollection> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_COLLECTION,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<ICollection> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_COLLECTION,
    payload: axios.delete(requestUrl)
  });
  return result;
};

export const setBlob = (name, data, contentType?) => ({
  type: ACTION_TYPES.SET_BLOB,
  payload: {
    name,
    data,
    contentType
  }
});

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
