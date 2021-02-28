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
      const from = new Date(dateFrom.getFullYear(), dateFrom.getMonth(), dateFrom.getDate(), 0, 0, 0)
      const to = new Date(dateTo.getFullYear(), dateTo.getMonth(), dateTo.getDate(), 23, 59, 59)
      return fetch(`/api/mentions?from=${from.toISOString()}&to=${to.toISOString()}`)
        .then(res => {
          commit('loaded')
          return res.status === 200 ? res.json() : reject(res)
        })
        .then(res => commit('setMentions', res.mentions))
    }
  },
  modules: {
  }
})
