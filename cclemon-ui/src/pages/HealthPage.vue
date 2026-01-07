<template>
  <q-page padding>
    <q-form @submit="onSubmit">
      <q-input
        v-model="form.date"
        mask="date"
        :rules="['date']"
        outlined
        dense
        class="q-mb-md"
      >
        <template v-slot:prepend>
          <q-icon name="event" class="cursor-pointer">
            <q-popup-proxy
              cover
              transition-show="scale"
              transition-hide="scale"
            >
              <q-date v-model="form.date">
                <div class="row items-center justify-end">
                  <q-btn v-close-popup label="Close" color="primary" flat />
                </div>
              </q-date>
            </q-popup-proxy>
          </q-icon>
        </template>
      </q-input>
      <q-input
        v-model="form.calories"
        type="number"
        label="熱量 (大卡)"
        :rules="[(val) => !!val || '請輸入熱量']"
        outlined
        dense
        class="q-mb-md"
      ></q-input>

      <q-input
        v-model="form.protein"
        type="number"
        label="蛋白質 (克)"
        :rules="[(val) => !!val || '請輸入蛋白質']"
        outlined
        dense
        class="q-mb-md"
      ></q-input>
      <q-btn
        type="submit"
        label="提交"
        color="primary"
        class="full-width"
      ></q-btn>
    </q-form>
  </q-page>
</template>

<script setup lang="ts">
import { reactive } from 'vue';
import { healthInfo } from './HealthPage.types';
import { test } from '../api/health/health';
const form = reactive<healthInfo>({
  date: '20241010',
  calories: 0,
  protein: 0,
});

const onSubmit = () => {
  console.log('form', form);
};

const init = async () => {
  await test();
};

init();
</script>

<style></style>
