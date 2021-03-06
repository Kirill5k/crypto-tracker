import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

const reject = (res) => res.json().then(e => Promise.reject(new Error(e.message)))

export default new Vuex.Store({
  state: {
    isLoading: false,
    dateFrom: null,
    dateTo: null,
    mentionsSummaries: []
  },
  mutations: {
    setMentions (state, mentions) {
      state.mentionsSummaries = mentions.summaries.slice(0, 20)
      state.dateFrom = mentions.dateRange.from
      state.dateTo = mentions.dateRange.to
    },
    loading (state) {
      state.isLoading = true
    },
    loaded (state) {
      state.isLoading = false
    }
  },
  actions: {
    getMentionsSummaries ({ commit, state }, { dateFrom, dateTo }) {
      return fetch(`/api/mentions/summary?from=${dateFrom}&to=${dateTo}`)
        .then(res => {
          commit('loaded')
          return res.status === 200 ? res.json() : reject(res)
        })
        .then(res => commit('setMentions', res))
    }
  },
  modules: {
  }
})
