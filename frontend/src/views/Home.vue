<template>
  <b-container>
    <date-range-picker
      @show="getMentions"
    />
    <mentions-popularity-chart
      v-if="mentions.length"
      :mentions="mentions"
    />
  </b-container>
</template>

<script>
import { BContainer } from 'bootstrap-vue'
import DateRangePicker from '@/components/DateRangePicker.vue'
import MentionsPopularityChart from '@/components/MentionsPopularityChart.vue'

export default {
  name: 'Home',
  components: { BContainer, DateRangePicker, MentionsPopularityChart },
  computed: {
    mentions () {
      return this.$store.state.mentions
    }
  },
  methods: {
    getMentions (dates) {
      this.$store.dispatch('getMentions', dates)
    }
  }
}
</script>
