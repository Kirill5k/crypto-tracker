<template>
  <div class="date-range">
    <label
      for="date-from"
      class="date-range__label"
    >
      Date from:
    </label>
    <b-form-datepicker
      id="date-from"
      :state="isValidDateFrom"
      class="date-range__date-picker"
      today-button
      reset-button
      v-model="dateFrom"
      value-as-date
      @input="select"
    />
    <label
      for="date-to"
      class="date-range__label"
    >
      Date to:
    </label>
    <b-form-datepicker
      id="date-to"
      :state="isValidDateTo"
      class="date-range__date-picker"
      today-button
      reset-button
      v-model="dateTo"
      value-as-date
      @input="select"
    />
  </div>
</template>

<script>
import { BFormDatepicker } from 'bootstrap-vue'

export default {
  name: 'DateRangePicker',
  components: { BFormDatepicker },
  props: {
    msg: String
  },
  data () {
    return {
      dateFrom: new Date(),
      dateTo: new Date()
    }
  },
  created () {
    this.select()
  },
  computed: {
    isValidDateFrom () {
      if (!this.dateFrom) {
        return false
      } else if (this.dateFrom && !this.dateTo) {
        return true
      } else {
        return this.dateFrom <= this.dateTo && this.daysBetweenDates() <= 90
      }
    },
    isValidDateTo () {
      if (!this.dateTo) {
        return false
      } else if (!this.dateFrom && this.dateTo) {
        return true
      } else {
        return this.dateTo >= this.dateFrom && this.daysBetweenDates() <= 90
      }
    }
  },
  methods: {
    select () {
      if (this.isValidDateTo && this.isValidDateFrom) {
        const from = new Date(this.dateFrom.getFullYear(), this.dateFrom.getMonth(), this.dateFrom.getDate(), 0, 0, 0)
        const to = new Date(this.dateTo.getFullYear(), this.dateTo.getMonth(), this.dateTo.getDate(), 23, 59, 59)
        this.$emit('select', { dateFrom: from.toISOString(), dateTo: to.toISOString() })
      }
    },
    daysBetweenDates () {
      const oneDay = 24 * 60 * 60 * 1000
      return Math.round(Math.abs((this.dateFrom - this.dateTo) / oneDay))
    }
  }
}
</script>

<style scoped lang="scss">
.date-range {
  display: flex;
  margin-top: 20px;

  &__label {
    width: 90px;
    text-align: right;
    padding-right: 10px;
  }

  &__date-picker {
    width: 350px;
  }
}
</style>
