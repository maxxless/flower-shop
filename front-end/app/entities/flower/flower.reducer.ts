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

import { IFlower, defaultValue } from 'app/shared/model/flower.model';

export const ACTION_TYPES = {
  SEARCH_FLOWERS: 'flower/SEARCH_FLOWERS',
  FETCH_FLOWER_LIST: 'flower/FETCH_FLOWER_LIST',
  FETCH_FLOWER: 'flower/FETCH_FLOWER',
  CREATE_FLOWER: 'flower/CREATE_FLOWER',
  UPDATE_FLOWER: 'flower/UPDATE_FLOWER',
  DELETE_FLOWER: 'flower/DELETE_FLOWER',
  SET_BLOB: 'flower/SET_BLOB',
  RESET: 'flower/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IFlower>,
  entity: defaultValue,
  links: { next: 0 },
  updating: false,
  totalItems: 0,
  updateSuccess: false
};

export type FlowerState = Readonly<typeof initialState>;

// Reducer

export default (state: FlowerState = initialState, action): FlowerState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_FLOWERS):
    case REQUEST(ACTION_TYPES.FETCH_FLOWER_LIST):
    case REQUEST(ACTION_TYPES.FETCH_FLOWER):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_FLOWER):
    case REQUEST(ACTION_TYPES.UPDATE_FLOWER):
    case REQUEST(ACTION_TYPES.DELETE_FLOWER):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.SEARCH_FLOWERS):
    case FAILURE(ACTION_TYPES.FETCH_FLOWER_LIST):
    case FAILURE(ACTION_TYPES.FETCH_FLOWER):
    case FAILURE(ACTION_TYPES.CREATE_FLOWER):
    case FAILURE(ACTION_TYPES.UPDATE_FLOWER):
    case FAILURE(ACTION_TYPES.DELETE_FLOWER):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.SEARCH_FLOWERS):
    case SUCCESS(ACTION_TYPES.FETCH_FLOWER_LIST): {
      const links = parseHeaderForLinks(action.payload.headers.link);

      return {
        ...state,
        loading: false,
        links,
        entities: loadMoreDataWhenScrolled(state.entities, action.payload.data, links),
        totalItems: parseInt(action.payload.headers['x-total-count'], 10)
      };
    }
    case SUCCESS(ACTION_TYPES.FETCH_FLOWER):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_FLOWER):
    case SUCCESS(ACTION_TYPES.UPDATE_FLOWER):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_FLOWER):
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

const apiUrl = 'api/flowers';
const apiSearchUrl = 'api/_search/flowers';

// Actions

export const getSearchEntities: ICrudSearchAction<IFlower> = (query, page, size, sort) => ({
  type: ACTION_TYPES.SEARCH_FLOWERS,
  payload: axios.get<IFlower>(`${apiSearchUrl}?query=${query}${sort ? `&page=${page}&size=${size}&sort=${sort}` : ''}`)
});

export const getEntities: ICrudGetAllAction<IFlower> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_FLOWER_LIST,
    payload: axios.get<IFlower>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`)
  };
};

export const getEntity: ICrudGetAction<IFlower> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_FLOWER,
    payload: axios.get<IFlower>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<IFlower> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_FLOWER,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  return result;
};

export const updateEntity: ICrudPutAction<IFlower> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_FLOWER,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<IFlower> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_FLOWER,
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
