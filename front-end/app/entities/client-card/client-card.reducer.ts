import axios from 'axios';
import { ICrudSearchAction, ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IClientCard, defaultValue } from 'app/shared/model/client-card.model';

export const ACTION_TYPES = {
  SEARCH_CLIENTCARDS: 'clientCard/SEARCH_CLIENTCARDS',
  FETCH_CLIENTCARD_LIST: 'clientCard/FETCH_CLIENTCARD_LIST',
  FETCH_CLIENTCARD: 'clientCard/FETCH_CLIENTCARD',
  CREATE_CLIENTCARD: 'clientCard/CREATE_CLIENTCARD',
  UPDATE_CLIENTCARD: 'clientCard/UPDATE_CLIENTCARD',
  DELETE_CLIENTCARD: 'clientCard/DELETE_CLIENTCARD',
  RESET: 'clientCard/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IClientCard>,
  entity: defaultValue,
  updating: false,
  updateSuccess: false
};

export type ClientCardState = Readonly<typeof initialState>;

// Reducer

export default (state: ClientCardState = initialState, action): ClientCardState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_CLIENTCARDS):
    case REQUEST(ACTION_TYPES.FETCH_CLIENTCARD_LIST):
    case REQUEST(ACTION_TYPES.FETCH_CLIENTCARD):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_CLIENTCARD):
    case REQUEST(ACTION_TYPES.UPDATE_CLIENTCARD):
    case REQUEST(ACTION_TYPES.DELETE_CLIENTCARD):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.SEARCH_CLIENTCARDS):
    case FAILURE(ACTION_TYPES.FETCH_CLIENTCARD_LIST):
    case FAILURE(ACTION_TYPES.FETCH_CLIENTCARD):
    case FAILURE(ACTION_TYPES.CREATE_CLIENTCARD):
    case FAILURE(ACTION_TYPES.UPDATE_CLIENTCARD):
    case FAILURE(ACTION_TYPES.DELETE_CLIENTCARD):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.SEARCH_CLIENTCARDS):
    case SUCCESS(ACTION_TYPES.FETCH_CLIENTCARD_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.FETCH_CLIENTCARD):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_CLIENTCARD):
    case SUCCESS(ACTION_TYPES.UPDATE_CLIENTCARD):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_CLIENTCARD):
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

const apiUrl = 'api/client-cards';
const apiSearchUrl = 'api/_search/client-cards';

// Actions

export const getSearchEntities: ICrudSearchAction<IClientCard> = (query, page, size, sort) => ({
  type: ACTION_TYPES.SEARCH_CLIENTCARDS,
  payload: axios.get<IClientCard>(`${apiSearchUrl}?query=${query}`)
});

export const getEntities: ICrudGetAllAction<IClientCard> = (page, size, sort) => ({
  type: ACTION_TYPES.FETCH_CLIENTCARD_LIST,
  payload: axios.get<IClientCard>(`${apiUrl}?cacheBuster=${new Date().getTime()}`)
});

export const getEntity: ICrudGetAction<IClientCard> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_CLIENTCARD,
    payload: axios.get<IClientCard>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<IClientCard> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_CLIENTCARD,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IClientCard> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_CLIENTCARD,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<IClientCard> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_CLIENTCARD,
    payload: axios.delete(requestUrl)
  });
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
