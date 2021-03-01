import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

const reject = (res) => res.json().then(e => Promise.reject(new Error(e.message)))

export default new Vuex.Store({
  state: {
    isLoading: false,
    mentions: []
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
        .then(res => commit('setMentions', res.summaries.slice(0, 20)))
    }
  },
  modules: {
  }
})
