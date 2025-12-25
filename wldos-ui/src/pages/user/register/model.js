import {fakeRegister} from './service';
import {setAuthority} from "@/utils/authority";

const Model = {
    namespace: 'userRegister',
    state: {
        status: undefined,
    },
    effects: {
        * submit({payload}, {call, put}) {
            const response = yield call(fakeRegister, payload);

            if (typeof response !== 'undefined' && response.data !== undefined && response.data.status === 'ok') {
                yield put({
                    type: 'registerHandle',
                    payload: {
                      ...response.data,
                      news: response.news || '注册成功！'
                    },
                });
            } else {
                yield put({
                    type: 'registerHandle',
                    payload: {
                        status: 'error',
                        news: response?.data?.news || '未知原因注册失败，请重试！'
                    },
                });
            }
        },
    },
    reducers: {
        registerHandle(state, {payload}) {
            // 注册成功，同时保存token自动登录
            if (payload && payload.currentAuthority)
                setAuthority(payload);
            return {...state, status: payload.status, news: payload.news};
        },
    },
};
export default Model;
