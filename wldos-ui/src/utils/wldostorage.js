const expirefix = "__expires__";
/**
 * @desc 在localStorage基础上封装支持过期时间的storage，代替cookie。
 * @author 树悉猿
 * @version 1.0
 */
class wldostorage {

    constructor() {
        this.source = localStorage;
        this.init();
    }

    init(){
        /**
        * @desc 初始化localstorage
        * @param {String} key 键
        * @param {String} value 值，若存储数组、对象，需要通过JSON.stringify转换为json字符串
        * @param {String} expired 过期时间，以分钟为单位
        */
        const reg = new RegExp(expirefix);
        const data = this.source;
        const list = Object.keys(data);
        for (let i = 0, len = list.length; i < len; i += 1) { // 据说，js用优化的for循环性能最高
          const item = list[i];
          const { key } = item;
          if( !reg.test(key)){
            const now = Date.now();
            const expires = data[`${key}${expirefix}`] || (Date.now + 1);
            if (now >= expires ) {
              this.remove(key);
            }
          }
        }
    }

    remove(key) {
        const data = this.source;
        const value = data[key];
        delete data[key];
        delete data[`${key}${expirefix}`];
        return value;
    }

    get(key) {
        /**
        * @desc 从localstorage获取项
        * @param {String} key 键
        * @param {String} expired 存储时为非必须字段，所以有可能取不到，默认为 Date.now+1
        */
        const { source } = this;

        const now = Date.now();

        const expired = source[`${key}${expirefix}`] || now+1;

        if ( now >= expired ) {
            this.remove(key);
            return undefined;
        }

        return source[key] ? JSON.parse(source[key]) : source[key];
    }


    set(key, value, expired) {
        /**
        * @desc 设置localStorage项
        * @param {String} key 键
        * @param {String} value 值
        * @param {Number} expired 过期时间，以分钟为单位，非必填
        */
        const { source } = this;
        source[key] = JSON.stringify(value);
        if (expired){
            source[`${key}${expirefix}`] = Date.now() + 1000*60*expired
        }
        return value;
    }
}

// eslint-disable-next-line new-cap
const wldosStorage = typeof window !== 'undefined' && new wldostorage();

export default wldosStorage;
