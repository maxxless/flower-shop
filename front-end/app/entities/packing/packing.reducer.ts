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

import { IPacking, defaultValue } from 'app/shared/model/packing.model';

export const ACTION_TYPES = {
  SEARCH_PACKINGS: 'packing/SEARCH_PACKINGS',
  FETCH_PACKING_LIST: 'packing/FETCH_PACKING_LIST',
  FETCH_PACKING: 'packing/FETCH_PACKING',
  CREATE_PACKING: 'packing/CREATE_PACKING',
  UPDATE_PACKING: 'packing/UPDATE_PACKING',
  DELETE_PACKING: 'packing/DELETE_PACKING',
  RESET: 'packing/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IPacking>,
  entity: defaultValue,
  links: { next: 0 },
  updating: false,
  totalItems: 0,
  updateSuccess: false
};

export type PackingState = Readonly<typeof initialState>;

// Reducer

export default (state: PackingState = initialState, action): PackingState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_PACKINGS):
    case REQUEST(ACTION_TYPES.FETCH_PACKING_LIST):
    case REQUEST(ACTION_TYPES.FETCH_PACKING):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_PACKING):
    case REQUEST(ACTION_TYPES.UPDATE_PACKING):
    case REQUEST(ACTION_TYPES.DELETE_PACKING):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.SEARCH_PACKINGS):
    case FAILURE(ACTION_TYPES.FETCH_PACKING_LIST):
    case FAILURE(ACTION_TYPES.FETCH_PACKING):
    case FAILURE(ACTION_TYPES.CREATE_PACKING):
    case FAILURE(ACTION_TYPES.UPDATE_PACKING):
    case FAILURE(ACTION_TYPES.DELETE_PACKING):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.SEARCH_PACKINGS):
    case SUCCESS(ACTION_TYPES.FETCH_PACKING_LIST): {
      const links = parseHeaderForLinks(action.payload.headers.link);

      return {
        ...state,
        loading: false,
        links,
        entities: loadMoreDataWhenScrolled(state.entities, action.payload.data, links),
        totalItems: parseInt(action.payload.headers['x-total-count'], 10)
      };
    }
    case SUCCESS(ACTION_TYPES.FETCH_PACKING):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_PACKING):
    case SUCCESS(ACTION_TYPES.UPDATE_PACKING):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_PACKING):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: {}
      };
    case ACTION_TYPES.RESET:
      return {
        ...initialState
      };
    default:
      return state;
  }
};

const apiUrl = 'api/packings';
const apiSearchUrl = 'api/_search/packings';

// Actions

export const getSearchEntities: ICrudSearchAction<IPacking> = (query, page, size, sort) => ({
  type: ACTION_TYPES.SEARCH_PACKINGS,
  payload: axios.get<IPacking>(`${apiSearchUrl}?query=${query}${sort ? `&page=${page}&size=${size}&sort=${sort}` : ''}`)
});

export const getEntities: ICrudGetAllAction<IPacking> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_PACKING_LIST,
    payload: axios.get<IPacking>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`)
  };
};

export const getEntity: ICrudGetAction<IPacking> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_PACKING,
    payload: axios.get<IPacking>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<IPacking> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_PACKING,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  return result;
};

export const updateEntity: ICrudPutAction<IPacking> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_PACKING,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<IPacking> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_PACKING,
    payload: axios.delete(requestUrl)
  });
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
