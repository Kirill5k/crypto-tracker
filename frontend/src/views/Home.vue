<template>
  <b-container>
    <date-range-picker
      @select="displayAllMentions"
    />
    <mentions-popularity-chart
      v-if="mentionsSummaries.length"
      :mentions="mentionsSummaries"
      @click="displayTickerMentionTimeseries"
    />
    <ticker-mentions-timeseries-chart
      v-if="tickerMentionTimes && tickerMentionTimes.length"
      :ticker="selectedTicker"
      :mention-times="tickerMentionTimes"
      :date-from="dateFrom"
      :date-to="dateTo"
      @click="displayTickerMentions"
      aria-controls="mentions-sidebar"
      :aria-expanded="toggleSidebar"
    />
    <mentions-sidebar
      :toggle="toggleSidebar"
    />
  </b-container>
</template>

<script>
import { BContainer } from 'bootstrap-vue'
import DateRangePicker from '@/components/DateRangePicker'
import MentionsPopularityChart from '@/components/MentionsPopularityChart'
import MentionsSidebar from '@/components/MentionsSidebar'
import TickerMentionsTimeseriesChart from '@/components/TickerMentionsTimeseriesChart'

export default {
  name: 'Home',
  components: { BContainer, DateRangePicker, MentionsPopularityChart, TickerMentionsTimeseriesChart, MentionsSidebar },
  data: () => ({
    selectedTicker: null,
    tickerMentionTimes: [],
    toggleSidebar: false
  }),
  computed: {
    mentionsSummaries () {
      return this.$store.state.mentionsSummaries
    },
    dateFrom () {
      return this.$store.state.dateFrom
    },
    dateTo () {
      return this.$store.state.dateTo
    }
  },
  methods: {
    displayAllMentions (dates) {
      this.$store.dispatch('getMentionsSummaries', dates).then(() => this.clearSelectedTicker())
    },
    displayTickerMentionTimeseries (ticker) {
      this.selectedTicker = ticker
      this.toggleSidebar = false
      this.tickerMentionTimes = this.mentionsSummaries.find(m => m.ticker === ticker)?.times
    },
    clearSelectedTicker () {
      this.tickerMentionTimes = []
      this.selectedTicker = null
      this.toggleSidebar = false
    },
    displayTickerMentions (tickerInfo) {
      console.log(tickerInfo)
      this.$store
        .dispatch('getTickerMentions', tickerInfo)
        .then(() => {
          this.toggleSidebar = true
        })
    }
  }
}
</script>
