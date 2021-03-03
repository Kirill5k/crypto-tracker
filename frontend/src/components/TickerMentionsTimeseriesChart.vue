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
  maintainAspectRatio: false,
  scales: {
    x: {
      type: 'time',
      time: {
        unit: 'hour',
        displayFormats: {
          quarter: 'HH, MM'
        }
      }
    }
  }
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
      console.log(this.mentionsCountedByHour)
      const labels = []
      const datasets = [{
        label: `${this.ticker} mentions`,
        backgroundColor: '#03c2fc',
        data: this.mentionsCountedByHour
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
    }
  }
}
</script>

<style scoped lang="scss">

</style>
