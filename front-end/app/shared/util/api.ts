import Axios from 'axios';

const getRequest = url => Axios.get(url).then(res => res.data);
const postRequest = (url, ...params) => Axios.post(url, ...params).then(res => res.data);

export const getAccount = () => getRequest('/api/account');
export const getAccountDetails = () => getRequest('/api/account/details');
export const getAccountOrders = () => getRequest('/api/account/orders');
export const getAccountDeliveries = () => getRequest('api/account/deliveries');

export const addCollectionToCart = (collectionId, body) => postRequest(`/api/collections/${collectionId}/cart`, body);
export const addFlowerToCart = (flowerId, body) => postRequest(`/api/flowers/${flowerId}/cart`, body);
export const makeOrder = body => postRequest('api/order', body);

export const getPackings = () => getRequest('/api/packings');
