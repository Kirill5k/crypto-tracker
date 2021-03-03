<template>
  <div class="mentions-chart">
    <bar-chart
      :chart-data="chartData"
      :options="options"
    />
  </div>
</template>

<script>
import BarChart from './charts/BarChart.js'

const DEFAULT_OPTIONS = {
  responsive: true,
  legend: {
    display: false
  },
  maintainAspectRatio: false
}

export default {
  name: 'MentionsPopularityChart',
  components: { BarChart },
  props: {
    mentions: {
      type: Array,
      required: true
    }
  },
  computed: {
    options () {
      return {
        ...DEFAULT_OPTIONS,
        onClick: (evt, item) => {
          if (item && item.length) {
            this.tickerClick(item[0]._view.label)
          }
        }
      }
    },
    chartData () {
      const labels = this.mentions.map(m => m.ticker)
      const datasets = [{
        label: 'Mentions',
        backgroundColor: '#03c2fc',
        data: this.mentions.map(m => m.total)
      }]
      return { labels, datasets }
    }
  },
  methods: {
    tickerClick (ticker) {
      this.$emit('click', ticker)
    }
  }
}
</script>

<style scoped lang="scss">

</style>
