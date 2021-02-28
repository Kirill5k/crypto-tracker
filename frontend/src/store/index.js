import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

const reject = (res) => res.json().then(e => Promise.reject(new Error(e.message)))

export default new Vuex.Store({
  state: {
    isLoading: false,
    // eslint-disable-next-line
    mentions: [{ticker:"AMC",total:113,times:["2021-02-28T17:24:07Z","2021-02-28T17:21:49Z","2021-02-28T17:20:56Z","2021-02-28T17:20:32Z","2021-02-28T17:08:47Z","2021-02-28T17:04:33Z","2021-02-28T17:04:06Z","2021-02-28T16:58:56Z","2021-02-28T16:56:28Z","2021-02-28T16:52:55Z","2021-02-28T16:50:53Z","2021-02-28T16:50:33Z","2021-02-28T16:39:14Z","2021-02-28T16:31:22Z","2021-02-28T16:27:05Z","2021-02-28T16:26:08Z","2021-02-28T16:09:47Z","2021-02-28T16:05:35Z","2021-02-28T16:05:30Z","2021-02-28T16:02:29Z","2021-02-28T15:30:22Z","2021-02-28T15:29:28Z","2021-02-28T15:21:50Z","2021-02-28T15:13:13Z","2021-02-28T15:07:21Z","2021-02-28T14:58:37Z","2021-02-28T14:56:45Z","2021-02-28T14:56:10Z","2021-02-28T14:21:04Z","2021-02-28T14:01:19Z","2021-02-28T13:49:52Z","2021-02-28T13:19:55Z","2021-02-28T13:12:39Z","2021-02-28T13:08:10Z","2021-02-28T12:59:38Z","2021-02-28T12:54:39Z","2021-02-28T12:39:26Z","2021-02-28T12:38:58Z","2021-02-28T12:37:35Z","2021-02-28T12:29:26Z","2021-02-28T12:01:31Z","2021-02-28T11:51:07Z","2021-02-28T11:44:39Z","2021-02-28T11:35:55Z","2021-02-28T11:25:41Z","2021-02-28T10:40:10Z","2021-02-28T10:30:12Z","2021-02-28T10:00:27Z","2021-02-28T09:42:02Z","2021-02-28T09:24:38Z","2021-02-28T09:18:56Z","2021-02-28T08:47:05Z","2021-02-28T08:43:59Z","2021-02-28T08:42:28Z","2021-02-28T08:26:41Z","2021-02-28T08:14:06Z","2021-02-28T07:58:33Z","2021-02-28T07:52:02Z","2021-02-28T07:42:13Z","2021-02-28T07:35:08Z","2021-02-28T07:26:32Z","2021-02-28T07:07:40Z","2021-02-28T06:59:59Z","2021-02-28T06:40:57Z","2021-02-28T06:34:55Z","2021-02-28T06:33:34Z","2021-02-28T06:27:02Z","2021-02-28T05:46:38Z","2021-02-28T05:40:33Z","2021-02-28T05:38:34Z","2021-02-28T05:18:53Z","2021-02-28T04:49:48Z","2021-02-28T04:49:24Z","2021-02-28T04:47:55Z","2021-02-28T04:40:04Z","2021-02-28T04:39:08Z","2021-02-28T04:24:58Z","2021-02-28T04:23:48Z","2021-02-28T04:22:25Z","2021-02-28T04:11:28Z","2021-02-28T03:56:23Z","2021-02-28T03:53:55Z","2021-02-28T03:53:14Z","2021-02-28T03:51:36Z","2021-02-28T03:15:18Z","2021-02-28T03:02:44Z","2021-02-28T02:52:26Z","2021-02-28T02:49:40Z","2021-02-28T02:43:12Z","2021-02-28T02:24:24Z","2021-02-28T02:22:53Z","2021-02-28T02:22:04Z","2021-02-28T02:15:58Z","2021-02-28T02:12:53Z","2021-02-28T02:06:47Z","2021-02-28T01:59:20Z","2021-02-28T01:55:39Z","2021-02-28T01:46:37Z","2021-02-28T01:28:29Z","2021-02-28T01:25:06Z","2021-02-28T01:23:54Z","2021-02-28T01:16:58Z","2021-02-28T01:14:20Z","2021-02-28T01:05:40Z","2021-02-28T00:51:50Z","2021-02-28T00:35:13Z","2021-02-28T00:34:22Z","2021-02-28T00:28:36Z","2021-02-28T00:18:00Z","2021-02-28T00:15:40Z","2021-02-28T00:11:17Z","2021-02-28T00:00:30Z","2021-02-28T00:00:17Z"]},{ticker:"GME",total:70,times:["2021-02-28T17:21:49Z","2021-02-28T17:21:22Z","2021-02-28T17:04:33Z","2021-02-28T16:52:55Z","2021-02-28T16:39:14Z","2021-02-28T16:31:22Z","2021-02-28T16:27:05Z","2021-02-28T16:26:08Z","2021-02-28T15:59:27Z","2021-02-28T15:58:23Z","2021-02-28T15:51:18Z","2021-02-28T15:39:28Z","2021-02-28T15:21:50Z","2021-02-28T15:03:21Z","2021-02-28T14:59:10Z","2021-02-28T14:34:13Z","2021-02-28T14:13:59Z","2021-02-28T13:58:16Z","2021-02-28T13:57:19Z","2021-02-28T13:51:23Z","2021-02-28T13:21:38Z","2021-02-28T13:13:09Z","2021-02-28T12:06:10Z","2021-02-28T11:46:17Z","2021-02-28T11:25:41Z","2021-02-28T11:24:26Z","2021-02-28T10:12:41Z","2021-02-28T10:02:08Z","2021-02-28T09:51:26Z","2021-02-28T09:49:53Z","2021-02-28T09:24:21Z","2021-02-28T09:18:56Z","2021-02-28T09:07:00Z","2021-02-28T08:45:17Z","2021-02-28T08:35:37Z","2021-02-28T08:01:05Z","2021-02-28T07:33:29Z","2021-02-28T07:22:12Z","2021-02-28T07:11:21Z","2021-02-28T07:11:08Z","2021-02-28T07:06:40Z","2021-02-28T07:00:11Z","2021-02-28T06:34:58Z","2021-02-28T06:27:02Z","2021-02-28T06:22:50Z","2021-02-28T05:53:40Z","2021-02-28T05:33:48Z","2021-02-28T04:45:17Z","2021-02-28T04:27:25Z","2021-02-28T04:18:19Z","2021-02-28T04:11:28Z","2021-02-28T04:05:33Z","2021-02-28T03:56:23Z","2021-02-28T03:53:55Z","2021-02-28T03:51:36Z","2021-02-28T03:46:10Z","2021-02-28T03:45:36Z","2021-02-28T03:42:30Z","2021-02-28T03:23:18Z","2021-02-28T03:16:52Z","2021-02-28T03:08:36Z","2021-02-28T02:49:34Z","2021-02-28T02:16:14Z","2021-02-28T01:16:58Z","2021-02-28T01:13:27Z","2021-02-28T00:28:36Z","2021-02-28T00:15:40Z","2021-02-28T00:14:18Z","2021-02-28T00:04:54Z","2021-02-28T00:03:33Z"]},{ticker:"PLTR",total:5,times:["2021-02-28T15:02:40Z","2021-02-28T12:02:07Z","2021-02-28T08:45:17Z","2021-02-28T05:46:38Z","2021-02-28T03:50:17Z"]},{ticker:"CCIV",total:4,times:["2021-02-28T12:02:07Z","2021-02-28T08:13:01Z","2021-02-28T03:49:06Z","2021-02-28T01:55:17Z"]},{ticker:"RKT",total:2,times:["2021-02-28T12:02:07Z","2021-02-28T10:35:10Z"]},{ticker:"SPY",total:2,times:["2021-02-28T06:03:14Z","2021-02-28T04:07:45Z"]},{ticker:"AAPL",total:1,times:["2021-02-28T08:45:17Z"]},{ticker:"WKHS",total:1,times:["2021-02-28T12:02:07Z"]},{ticker:"TLRY",total:1,times:["2021-02-28T05:46:38Z"]},{ticker:"NOK",total:1,times:["2021-02-28T03:20:17Z"]},{ticker:"TD",total:1,times:["2021-02-28T06:35:07Z"]},{ticker:"FUBO",total:1,times:["2021-02-28T07:42:29Z"]},{ticker:"PMPG",total:1,times:["2021-02-28T01:21:39Z"]},{ticker:"CRSR",total:1,times:["2021-02-28T05:56:16Z"]},{ticker:"FROG",total:1,times:["2021-02-28T03:20:57Z"]},{ticker:"BB",total:1,times:["2021-02-28T05:46:38Z"]},{ticker:"SNDL",total:1,times:["2021-02-28T05:46:38Z"]},{ticker:"TSNP",total:1,times:["2021-02-28T00:52:44Z"]},{ticker:"CBLAQ",total:1,times:["2021-02-28T02:43:25Z"]},{ticker:"BABA",total:1,times:["2021-02-28T14:20:59Z"]},{ticker:"IDEX",total:1,times:["2021-02-28T15:59:09Z"]}]
  },
  mutations: {
    setMentions (state, mentions) {
      state.mentions = mentions
    },
    loading (state) {
      state.isLoading = true
    },
    loaded (state) {
      state.isLoading = false
    }
  },
  actions: {
    getMentions ({ commit, state }, { dateFrom, dateTo }) {
      return fetch(`/api/mentions/summary?from=${dateFrom.toISOString()}&to=${dateTo.toISOString()}`)
        .then(res => {
          commit('loaded')
          return res.status === 200 ? res.json() : reject(res)
        })
        .then(res => commit('setMentions', res.summaries))
    }
  },
  modules: {
  }
})
