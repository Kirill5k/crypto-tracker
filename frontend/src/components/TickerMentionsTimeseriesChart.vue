<template>
  <div class="ticker-mentions-timeseries-chart">
    <line-chart
      :ticker="ticker"
      :chart-data="chartData"
      :options="options"
    />
  </div>
</template>

<script>
import LineChart from './charts/LineChart.js'

const DEFAULT_OPTIONS = {
  responsive: true,
  legend: {
    display: true
  },
  maintainAspectRatio: false
}

export default {
  name: 'TickerMentionsTimeseriesChart',
  components: { LineChart },
  props: {
    ticker: {
      type: String,
      required: true
    },
    mentionTimes: {
      type: Array,
      required: true
    },
    dateFrom: {
      type: Date,
      required: true
    },
    dateTo: {
      type: Date,
      required: true
    }
  },
  computed: {
    options () {
      return {
        ...DEFAULT_OPTIONS
      }
    },
    chartData () {
      const mentionsByHour = this.mentionsCountedByHour
      const labels = this.labels
      const datasets = [{
        label: `${this.ticker} mentions`,
        backgroundColor: '#03c2fc',
        data: labels.map(d => mentionsByHour[d] || 0)
      }]
      return { labels, datasets }
    },
    mentionsCountedByHour () {
      return this.mentionTimes
        .map(t => new Date(t))
        .reduce((acc, el) => {
          const key = el.toISOString().slice(0, 13)
          acc[key] = !acc[key] ? 1 : acc[key] + 1
          return acc
        }, {})
    },
    labels () {
      const dates = []
      for (let d = this.dateFrom; d <= this.dateTo; d.setHours(d.getHours() + 1)) {
        dates.push(new Date(d))
      }
      return dates.map(d => d.toISOString().slice(0, 13))
    }
  }
}
</script>

<style scoped lang="scss">

</style>
