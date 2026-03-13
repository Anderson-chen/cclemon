import { healthApi } from 'src/boot/axios';

export async function test(): Promise<string> {
  return (await healthApi.get('test')).data;
}
